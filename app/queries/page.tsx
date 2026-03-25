'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

interface QueryResult {
  [key: string]: any;
}

const QUERIES = [
  { id: 'available-books', label: 'Available Books (Q2)', description: 'List all books with available copies' },
  { id: 'overdue-members', label: 'Overdue Members (Q3)', description: 'Members who have not returned books on time' },
  { id: 'librarian-issues', label: 'Librarian Issues (Q4)', description: 'Total number of books issued by each librarian' },
  { id: 'most-active-member', label: 'Most Active Member (Q5)', description: 'Member who has issued the most books' },
  { id: 'popular-books', label: 'Popular Books (Q6)', description: 'Books with total copies greater than 2' },
  { id: 'recent-members', label: 'Recent Members (Q7)', description: 'Members whose membership date is recent' },
  { id: 'issue-summary', label: 'Issue Summary (Q8)', description: 'Book issue summary view' },
  { id: 'member-summary', label: 'Member Activity (Q9)', description: 'Member activity summary view' },
  { id: 'issues-by-month', label: 'Issues by Month (Q10)', description: 'Books issued in each month' },
  { id: 'inactive-librarians', label: 'Inactive Librarians (Q11)', description: 'Librarians with zero issues' },
  { id: 'avg-books-per-member', label: 'Avg Books/Member (Q12)', description: 'Average books per member' },
  { id: 'most-issued-book', label: 'Most Issued Book (Q13)', description: 'The book with highest circulation' },
  { id: 'active-issues', label: 'Active Issues (Q14)', description: 'Members with active issues' },
  { id: 'inventory-summary', label: 'Inventory Summary (Q15)', description: 'Overall book inventory summary' },
];

export default function QueriesPage() {
  const [selectedQuery, setSelectedQuery] = useState<string | null>(null);
  const [results, setResults] = useState<QueryResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const executeQuery = async (queryId: string) => {
    setSelectedQuery(queryId);
    setIsLoading(true);
    try {
      const response = await fetch(`/api/queries?q=${queryId}`);
      const data = await response.json();
      setResults(Array.isArray(data) ? data : [data]);
    } catch (error) {
      console.error('Error executing query:', error);
      setResults([]);
    } finally {
      setIsLoading(false);
    }
  };

  const currentQuery = QUERIES.find(q => q.id === selectedQuery);

  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="border-b border-border">
        <div className="max-w-7xl mx-auto px-6 py-6">
          <Link href="/">
            <Button variant="ghost" className="mb-4">← Back</Button>
          </Link>
          <h1 className="text-3xl font-serif font-bold text-primary">Queries</h1>
          <p className="text-muted-foreground mt-2">Database analysis and reports</p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="grid md:grid-cols-3 gap-6">
          {/* Query Selection */}
          <div className="md:col-span-1">
            <Card className="bg-card border-border sticky top-6">
              <CardHeader>
                <CardTitle className="text-lg">Queries</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                {QUERIES.map((query) => (
                  <Button
                    key={query.id}
                    variant={selectedQuery === query.id ? 'default' : 'ghost'}
                    onClick={() => executeQuery(query.id)}
                    className={`w-full justify-start text-left ${
                      selectedQuery === query.id
                        ? 'bg-primary text-primary-foreground hover:bg-accent'
                        : 'hover:bg-secondary'
                    }`}
                  >
                    <div>
                      <div className="text-sm font-mono">{query.label}</div>
                    </div>
                  </Button>
                ))}
              </CardContent>
            </Card>
          </div>

          {/* Results */}
          <div className="md:col-span-2">
            {!selectedQuery ? (
              <Card className="bg-card border-border">
                <CardContent className="pt-6">
                  <div className="text-center py-12">
                    <p className="text-muted-foreground">Select a query to view results</p>
                  </div>
                </CardContent>
              </Card>
            ) : (
              <Card className="bg-card border-border">
                <CardHeader>
                  <CardTitle className="text-xl">{currentQuery?.label}</CardTitle>
                  <p className="text-sm text-muted-foreground mt-2">{currentQuery?.description}</p>
                </CardHeader>
                <CardContent>
                  {isLoading ? (
                    <div className="text-center py-8 text-muted-foreground">Loading results...</div>
                  ) : results.length === 0 ? (
                    <div className="text-center py-8 text-muted-foreground">No results found</div>
                  ) : (
                    <div className="overflow-x-auto">
                      <Table>
                        <TableHeader>
                          <TableRow className="border-border hover:bg-secondary">
                            {Object.keys(results[0]).map((key) => (
                              <TableHead key={key} className="text-foreground font-mono text-xs">
                                {key}
                              </TableHead>
                            ))}
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {results.map((row, idx) => (
                            <TableRow key={idx} className="border-border hover:bg-secondary">
                              {Object.values(row).map((value, cellIdx) => (
                                <TableCell key={cellIdx} className="font-mono text-sm">
                                  {typeof value === 'string' && value.includes('T')
                                    ? new Date(value).toLocaleDateString()
                                    : String(value)}
                                </TableCell>
                              ))}
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </div>
                  )}
                </CardContent>
              </Card>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
