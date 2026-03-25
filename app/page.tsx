'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

interface DashboardStats {
  totalBooks: number;
  totalMembers: number;
  totalLibrarians: number;
  totalIssues: number;
  availableBooks: number;
}

export default function Dashboard() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [books, members, librarians, issues, available] = await Promise.all([
          fetch('/api/books').then(r => r.json()),
          fetch('/api/members').then(r => r.json()),
          fetch('/api/librarians').then(r => r.json()),
          fetch('/api/issues').then(r => r.json()),
          fetch('/api/queries?q=available-books').then(r => r.json()),
        ]);

        setStats({
          totalBooks: books?.length || 0,
          totalMembers: members?.length || 0,
          totalLibrarians: librarians?.length || 0,
          totalIssues: issues?.length || 0,
          availableBooks: available?.length || 0,
        });
      } catch (error) {
        console.error('Error fetching stats:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchStats();
  }, []);

  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="border-b border-border">
        <div className="max-w-7xl mx-auto px-6 py-8">
          <h1 className="text-4xl font-serif font-bold text-primary mb-2">Library Management</h1>
          <p className="text-muted-foreground">Comprehensive inventory and circulation system</p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-12">
        {/* Stats Grid */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4 mb-12">
          <Card className="bg-card border-border">
            <CardContent className="pt-6">
              <div className="text-center">
                <div className="text-3xl font-mono font-bold text-primary mb-2">{stats?.totalBooks || 0}</div>
                <div className="text-sm text-muted-foreground font-mono">Total Books</div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardContent className="pt-6">
              <div className="text-center">
                <div className="text-3xl font-mono font-bold text-primary mb-2">{stats?.availableBooks || 0}</div>
                <div className="text-sm text-muted-foreground font-mono">Available</div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardContent className="pt-6">
              <div className="text-center">
                <div className="text-3xl font-mono font-bold text-primary mb-2">{stats?.totalMembers || 0}</div>
                <div className="text-sm text-muted-foreground font-mono">Members</div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardContent className="pt-6">
              <div className="text-center">
                <div className="text-3xl font-mono font-bold text-primary mb-2">{stats?.totalLibrarians || 0}</div>
                <div className="text-sm text-muted-foreground font-mono">Librarians</div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardContent className="pt-6">
              <div className="text-center">
                <div className="text-3xl font-mono font-bold text-primary mb-2">{stats?.totalIssues || 0}</div>
                <div className="text-sm text-muted-foreground font-mono">Issues</div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Navigation Cards */}
        <div className="grid md:grid-cols-2 gap-6">
          <Card className="bg-card border-border hover:border-primary transition-colors">
            <CardHeader>
              <CardTitle className="text-xl font-serif">Book Management</CardTitle>
              <CardDescription>Manage library inventory</CardDescription>
            </CardHeader>
            <CardContent>
              <Link href="/books">
                <Button className="w-full bg-primary text-primary-foreground hover:bg-accent">
                  Manage Books
                </Button>
              </Link>
            </CardContent>
          </Card>

          <Card className="bg-card border-border hover:border-primary transition-colors">
            <CardHeader>
              <CardTitle className="text-xl font-serif">Member Management</CardTitle>
              <CardDescription>Manage library members</CardDescription>
            </CardHeader>
            <CardContent>
              <Link href="/members">
                <Button className="w-full bg-primary text-primary-foreground hover:bg-accent">
                  Manage Members
                </Button>
              </Link>
            </CardContent>
          </Card>

          <Card className="bg-card border-border hover:border-primary transition-colors">
            <CardHeader>
              <CardTitle className="text-xl font-serif">Librarian Management</CardTitle>
              <CardDescription>Manage staff accounts</CardDescription>
            </CardHeader>
            <CardContent>
              <Link href="/librarians">
                <Button className="w-full bg-primary text-primary-foreground hover:bg-accent">
                  Manage Librarians
                </Button>
              </Link>
            </CardContent>
          </Card>

          <Card className="bg-card border-border hover:border-primary transition-colors">
            <CardHeader>
              <CardTitle className="text-xl font-serif">Issue Management</CardTitle>
              <CardDescription>Track book circulation</CardDescription>
            </CardHeader>
            <CardContent>
              <Link href="/issues">
                <Button className="w-full bg-primary text-primary-foreground hover:bg-accent">
                  Manage Issues
                </Button>
              </Link>
            </CardContent>
          </Card>

          <Card className="bg-card border-border hover:border-primary transition-colors">
            <CardHeader>
              <CardTitle className="text-xl font-serif">Queries</CardTitle>
              <CardDescription>View analytics and reports</CardDescription>
            </CardHeader>
            <CardContent>
              <Link href="/queries">
                <Button className="w-full bg-primary text-primary-foreground hover:bg-accent">
                  View Reports
                </Button>
              </Link>
            </CardContent>
          </Card>

          <Card className="bg-card border-border hover:border-primary transition-colors">
            <CardHeader>
              <CardTitle className="text-xl font-serif">Database Views</CardTitle>
              <CardDescription>View materialized summaries</CardDescription>
            </CardHeader>
            <CardContent>
              <Link href="/views">
                <Button className="w-full bg-primary text-primary-foreground hover:bg-accent">
                  View Summaries
                </Button>
              </Link>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}
