'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

interface Librarian {
  id: number;
  name: string;
  email: string;
  phone: string;
  employee_id: string;
  hire_date: string;
}

export default function LibrariansPage() {
  const [librarians, setLibrarians] = useState<Librarian[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    employee_id: '',
  });

  useEffect(() => {
    fetchLibrarians();
  }, []);

  const fetchLibrarians = async () => {
    try {
      const response = await fetch('/api/librarians');
      const data = await response.json();
      setLibrarians(data);
    } catch (error) {
      console.error('Error fetching librarians:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/librarians', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });
      
      if (response.ok) {
        await fetchLibrarians();
        setShowForm(false);
        setFormData({ name: '', email: '', phone: '', employee_id: '' });
      }
    } catch (error) {
      console.error('Error creating librarian:', error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await fetch(`/api/librarians/${id}`, { method: 'DELETE' });
      await fetchLibrarians();
    } catch (error) {
      console.error('Error deleting librarian:', error);
    }
  };

  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="border-b border-border">
        <div className="max-w-7xl mx-auto px-6 py-6">
          <Link href="/">
            <Button variant="ghost" className="mb-4">← Back</Button>
          </Link>
          <h1 className="text-3xl font-serif font-bold text-primary">Librarians</h1>
          <p className="text-muted-foreground mt-2">Manage library staff</p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-serif font-bold">Staff Directory</h2>
          <Button 
            onClick={() => setShowForm(!showForm)}
            className="bg-primary text-primary-foreground hover:bg-accent"
          >
            {showForm ? 'Cancel' : '+ Add Librarian'}
          </Button>
        </div>

        {showForm && (
          <Card className="bg-card border-border mb-6">
            <CardHeader>
              <CardTitle>Hire New Librarian</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid md:grid-cols-2 gap-4">
                  <Input
                    placeholder="Full Name"
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    className="bg-secondary text-foreground border-border"
                    required
                  />
                  <Input
                    placeholder="Employee ID"
                    value={formData.employee_id}
                    onChange={(e) => setFormData({...formData, employee_id: e.target.value})}
                    className="bg-secondary text-foreground border-border"
                    required
                  />
                  <Input
                    placeholder="Email"
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                    className="bg-secondary text-foreground border-border"
                    required
                  />
                  <Input
                    placeholder="Phone"
                    value={formData.phone}
                    onChange={(e) => setFormData({...formData, phone: e.target.value})}
                    className="bg-secondary text-foreground border-border"
                  />
                </div>
                <Button 
                  type="submit"
                  className="bg-primary text-primary-foreground hover:bg-accent"
                >
                  Add Librarian
                </Button>
              </form>
            </CardContent>
          </Card>
        )}

        <Card className="bg-card border-border">
          <CardContent className="pt-6">
            {isLoading ? (
              <div className="text-center py-8 text-muted-foreground">Loading librarians...</div>
            ) : librarians.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">No librarians found</div>
            ) : (
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow className="border-border hover:bg-secondary">
                      <TableHead className="text-foreground font-mono">Name</TableHead>
                      <TableHead className="text-foreground font-mono">Employee ID</TableHead>
                      <TableHead className="text-foreground font-mono">Email</TableHead>
                      <TableHead className="text-foreground font-mono">Phone</TableHead>
                      <TableHead className="text-foreground font-mono">Hired</TableHead>
                      <TableHead className="text-foreground font-mono">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {librarians.map((lib) => (
                      <TableRow key={lib.id} className="border-border hover:bg-secondary">
                        <TableCell className="font-serif">{lib.name}</TableCell>
                        <TableCell className="font-mono text-primary">{lib.employee_id}</TableCell>
                        <TableCell className="font-mono text-muted-foreground">{lib.email}</TableCell>
                        <TableCell className="font-mono">{lib.phone}</TableCell>
                        <TableCell className="font-mono text-muted-foreground">{new Date(lib.hire_date).toLocaleDateString()}</TableCell>
                        <TableCell>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleDelete(lib.id)}
                            className="text-destructive hover:bg-destructive/10"
                          >
                            Delete
                          </Button>
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
