'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

interface Issue {
  id: number;
  book_id: number;
  book_title: string;
  member_id: number;
  member_name: string;
  librarian_id: number;
  librarian_name: string;
  issue_date: string;
  due_date: string;
  return_date: string | null;
}

interface Book {
  id: number;
  title: string;
}

interface Member {
  id: number;
  name: string;
}

interface Librarian {
  id: number;
  name: string;
}

export default function IssuesPage() {
  const [issues, setIssues] = useState<Issue[]>([]);
  const [books, setBooks] = useState<Book[]>([]);
  const [members, setMembers] = useState<Member[]>([]);
  const [librarians, setLibrarians] = useState<Librarian[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    book_id: '',
    member_id: '',
    librarian_id: '',
    due_date: '',
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [issuesRes, booksRes, membersRes, librariansRes] = await Promise.all([
        fetch('/api/issues'),
        fetch('/api/books'),
        fetch('/api/members'),
        fetch('/api/librarians'),
      ]);
      
      setIssues(await issuesRes.json());
      setBooks(await booksRes.json());
      setMembers(await membersRes.json());
      setLibrarians(await librariansRes.json());
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/issues', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          book_id: parseInt(formData.book_id),
          member_id: parseInt(formData.member_id),
          librarian_id: parseInt(formData.librarian_id),
          due_date: formData.due_date,
        }),
      });
      
      if (response.ok) {
        await fetchData();
        setShowForm(false);
        setFormData({ book_id: '', member_id: '', librarian_id: '', due_date: '' });
      }
    } catch (error) {
      console.error('Error creating issue:', error);
    }
  };

  const handleReturn = async (id: number) => {
    try {
      const response = await fetch(`/api/issues/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ return_date: new Date().toISOString() }),
      });
      
      if (response.ok) {
        await fetchData();
      }
    } catch (error) {
      console.error('Error returning issue:', error);
    }
  };

  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="border-b border-border">
        <div className="max-w-7xl mx-auto px-6 py-6">
          <Link href="/">
            <Button variant="ghost" className="mb-4">← Back</Button>
          </Link>
          <h1 className="text-3xl font-serif font-bold text-primary">Issues</h1>
          <p className="text-muted-foreground mt-2">Manage book circulation</p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-serif font-bold">Issue Records</h2>
          <Button 
            onClick={() => setShowForm(!showForm)}
            className="bg-primary text-primary-foreground hover:bg-accent"
          >
            {showForm ? 'Cancel' : '+ Issue Book'}
          </Button>
        </div>

        {showForm && (
          <Card className="bg-card border-border mb-6">
            <CardHeader>
              <CardTitle>Issue New Book</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid md:grid-cols-2 gap-4">
                  <Select value={formData.book_id} onValueChange={(value) => setFormData({...formData, book_id: value})}>
                    <SelectTrigger className="bg-secondary text-foreground border-border">
                      <SelectValue placeholder="Select Book" />
                    </SelectTrigger>
                    <SelectContent className="bg-secondary text-foreground border-border">
                      {books.map((book) => (
                        <SelectItem key={book.id} value={book.id.toString()}>
                          {book.title}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>

                  <Select value={formData.member_id} onValueChange={(value) => setFormData({...formData, member_id: value})}>
                    <SelectTrigger className="bg-secondary text-foreground border-border">
                      <SelectValue placeholder="Select Member" />
                    </SelectTrigger>
                    <SelectContent className="bg-secondary text-foreground border-border">
                      {members.map((member) => (
                        <SelectItem key={member.id} value={member.id.toString()}>
                          {member.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>

                  <Select value={formData.librarian_id} onValueChange={(value) => setFormData({...formData, librarian_id: value})}>
                    <SelectTrigger className="bg-secondary text-foreground border-border">
                      <SelectValue placeholder="Select Librarian" />
                    </SelectTrigger>
                    <SelectContent className="bg-secondary text-foreground border-border">
                      {librarians.map((lib) => (
                        <SelectItem key={lib.id} value={lib.id.toString()}>
                          {lib.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>

                  <Input
                    placeholder="Due Date"
                    type="date"
                    value={formData.due_date}
                    onChange={(e) => setFormData({...formData, due_date: e.target.value})}
                    className="bg-secondary text-foreground border-border"
                    required
                  />
                </div>
                <Button 
                  type="submit"
                  className="bg-primary text-primary-foreground hover:bg-accent"
                >
                  Issue Book
                </Button>
              </form>
            </CardContent>
          </Card>
        )}

        <Card className="bg-card border-border">
          <CardContent className="pt-6">
            {isLoading ? (
              <div className="text-center py-8 text-muted-foreground">Loading issues...</div>
            ) : issues.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">No issues found</div>
            ) : (
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow className="border-border hover:bg-secondary">
                      <TableHead className="text-foreground font-mono">Book</TableHead>
                      <TableHead className="text-foreground font-mono">Member</TableHead>
                      <TableHead className="text-foreground font-mono">Librarian</TableHead>
                      <TableHead className="text-foreground font-mono">Issued</TableHead>
                      <TableHead className="text-foreground font-mono">Due</TableHead>
                      <TableHead className="text-foreground font-mono">Returned</TableHead>
                      <TableHead className="text-foreground font-mono">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {issues.map((issue) => (
                      <TableRow key={issue.id} className="border-border hover:bg-secondary">
                        <TableCell className="font-serif text-sm">{issue.book_title}</TableCell>
                        <TableCell className="font-serif text-sm">{issue.member_name}</TableCell>
                        <TableCell className="font-serif text-sm">{issue.librarian_name}</TableCell>
                        <TableCell className="font-mono text-muted-foreground text-sm">{new Date(issue.issue_date).toLocaleDateString()}</TableCell>
                        <TableCell className="font-mono text-sm">{new Date(issue.due_date).toLocaleDateString()}</TableCell>
                        <TableCell className="font-mono text-muted-foreground text-sm">
                          {issue.return_date ? new Date(issue.return_date).toLocaleDateString() : 'Not returned'}
                        </TableCell>
                        <TableCell>
                          {!issue.return_date && (
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => handleReturn(issue.id)}
                              className="text-primary hover:bg-primary/10"
                            >
                              Mark Returned
                            </Button>
                          )}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
          </CardContent>
        </Card>
      </main>
    </div>
  );
}
