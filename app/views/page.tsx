'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

interface ViewData {
  [key: string]: any;
}

export default function ViewsPage() {
  const [issueSummary, setIssueSummary] = useState<ViewData[]>([]);
  const [memberSummary, setMemberSummary] = useState<ViewData[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchViews();
  }, []);

  const fetchViews = async () => {
    try {
      const [issuRes, memberRes] = await Promise.all([
        fetch('/api/queries?q=issue-summary'),
        fetch('/api/queries?q=member-summary'),
      ]);
      
      setIssueSummary(await issuRes.json());
      setMemberSummary(await memberRes.json());
    } catch (error) {
      console.error('Error fetching views:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="border-b border-border">
        <div className="max-w-7xl mx-auto px-6 py-6">
          <Link href="/">
            <Button variant="ghost" className="mb-4">← Back</Button>
          </Link>
          <h1 className="text-3xl font-serif font-bold text-primary">Database Views</h1>
          <p className="text-muted-foreground mt-2">Materialized summary views</p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-8">
        {isLoading ? (
          <div className="text-center py-12 text-muted-foreground">Loading views...</div>
        ) : (
          <Tabs defaultValue="issues" className="w-full">
            <TabsList className="bg-secondary border-border">
              <TabsTrigger value="issues" className="font-mono">Book Issue Summary</TabsTrigger>
              <TabsTrigger value="members" className="font-mono">Member Activity Summary</TabsTrigger>
            </TabsList>

            <TabsContent value="issues" className="mt-6">
              <Card className="bg-card border-border">
                <CardHeader>
                  <CardTitle className="text-xl">Book Issue Summary</CardTitle>
                  <p className="text-sm text-muted-foreground mt-2">Overview of book circulation statistics</p>
                </CardHeader>
                <CardContent>
                  {issueSummary.length === 0 ? (
                    <div className="text-center py-8 text-muted-foreground">No data available</div>
                  ) : (
                    <div className="overflow-x-auto">
                      <Table>
                        <TableHeader>
                          <TableRow className="border-border hover:bg-secondary">
                            {Object.keys(issueSummary[0]).map((key) => (
                              <TableHead key={key} className="text-foreground font-mono text-xs">
                                {key.replace(/_/g, ' ').toUpperCase()}
                              </TableHead>
                            ))}
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {issueSummary.map((row, idx) => (
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
            </TabsContent>

            <TabsContent value="members" className="mt-6">
              <Card className="bg-card border-border">
                <CardHeader>
                  <CardTitle className="text-xl">Member Activity Summary</CardTitle>
                  <p className="text-sm text-muted-foreground mt-2">Member engagement and borrowing statistics</p>
                </CardHeader>
                <CardContent>
                  {memberSummary.length === 0 ? (
                    <div className="text-center py-8 text-muted-foreground">No data available</div>
                  ) : (
                    <div className="overflow-x-auto">
                      <Table>
                        <TableHeader>
                          <TableRow className="border-border hover:bg-secondary">
                            {Object.keys(memberSummary[0]).map((key) => (
                              <TableHead key={key} className="text-foreground font-mono text-xs">
                                {key.replace(/_/g, ' ').toUpperCase()}
                              </TableHead>
                            ))}
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {memberSummary.map((row, idx) => (
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
            </TabsContent>
          </Tabs>
        )}
      </main>
    </div>
  );
}
