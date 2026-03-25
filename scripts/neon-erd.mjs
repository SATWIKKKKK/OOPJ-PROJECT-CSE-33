import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { Pool } from '@neondatabase/serverless';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const projectRoot = path.resolve(__dirname, '..');

async function loadEnvLocal() {
  const envPath = path.join(projectRoot, '.env.local');
  const raw = await fs.readFile(envPath, 'utf8');
  for (const line of raw.split(/\r?\n/)) {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith('#')) continue;
    const idx = trimmed.indexOf('=');
    if (idx === -1) continue;
    const key = trimmed.slice(0, idx).trim();
    const val = trimmed.slice(idx + 1).trim();
    if (!(key in process.env)) process.env[key] = val;
  }
}

function sanitizeType(t) {
  return t.replace(/\s+/g, '_').replace(/[()]/g, '');
}

async function run() {
  await loadEnvLocal();
  if (!process.env.DATABASE_URL) throw new Error('DATABASE_URL missing');

  const pool = new Pool({ connectionString: process.env.DATABASE_URL });

  try {
    const tablesRes = await pool.query(
      `SELECT table_name
       FROM information_schema.tables
       WHERE table_schema = 'public'
         AND table_type = 'BASE TABLE'
         AND table_name IN ('books_5900', 'members_5900', 'librarians_5900', 'issue_records_5900')
       ORDER BY table_name`
    );

    const columnsRes = await pool.query(
      `SELECT table_name, column_name, data_type
       FROM information_schema.columns
       WHERE table_schema = 'public'
         AND table_name IN ('books_5900', 'members_5900', 'librarians_5900', 'issue_records_5900')
       ORDER BY table_name, ordinal_position`
    );

    const fkRes = await pool.query(
      `SELECT
         tc.table_name AS child_table,
         kcu.column_name AS child_column,
         ccu.table_name AS parent_table,
         ccu.column_name AS parent_column
       FROM information_schema.table_constraints tc
       JOIN information_schema.key_column_usage kcu
         ON tc.constraint_name = kcu.constraint_name
        AND tc.table_schema = kcu.table_schema
       JOIN information_schema.constraint_column_usage ccu
         ON ccu.constraint_name = tc.constraint_name
        AND ccu.table_schema = tc.table_schema
       WHERE tc.constraint_type = 'FOREIGN KEY'
         AND tc.table_schema = 'public'
         AND tc.table_name IN ('issue_records_5900')
       ORDER BY child_table, child_column`
    );

    const tables = tablesRes.rows.map((r) => r.table_name);
    const colsByTable = new Map();
    for (const row of columnsRes.rows) {
      if (!colsByTable.has(row.table_name)) colsByTable.set(row.table_name, []);
      colsByTable.get(row.table_name).push(`${row.column_name} ${sanitizeType(row.data_type)}`);
    }

    const lines = ['erDiagram'];
    for (const table of tables) {
      lines.push(`  ${table} {`);
      for (const col of colsByTable.get(table) ?? []) {
        lines.push(`    ${col}`);
      }
      lines.push('  }');
    }

    for (const fk of fkRes.rows) {
      lines.push(`  ${fk.parent_table} ||--o{ ${fk.child_table} : "${fk.child_column} -> ${fk.parent_column}"`);
    }

    const outPath = path.join(projectRoot, 'ERD.md');
    await fs.writeFile(outPath, `${lines.join('\n')}\n`, 'utf8');
    console.log(`ER diagram generated at ${outPath}`);
  } finally {
    await pool.end();
  }
}

run().catch((err) => {
  console.error('ERD generation failed:', err.message);
  process.exit(1);
});
