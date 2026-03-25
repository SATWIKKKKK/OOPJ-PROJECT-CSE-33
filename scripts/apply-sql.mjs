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

async function run() {
  await loadEnvLocal();
  const connectionString = process.env.DATABASE_URL;
  if (!connectionString) throw new Error('DATABASE_URL missing');

  const pool = new Pool({ connectionString });
  const schemaSql = await fs.readFile(path.join(projectRoot, 'scripts', '01-schema.sql'), 'utf8');
  const viewsSql = await fs.readFile(path.join(projectRoot, 'scripts', '02-views-triggers.sql'), 'utf8');

  try {
    await pool.query(schemaSql);
    await pool.query(viewsSql);
    console.log('Applied schema and views/triggers successfully.');
  } finally {
    await pool.end();
  }
}

run().catch((err) => {
  console.error('SQL apply failed:', err.message);
  process.exit(1);
});
