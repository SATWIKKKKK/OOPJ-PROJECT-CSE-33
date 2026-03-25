'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  publication_year: number;
  total_copies: number;
  available_copies: number;
}

export default function BooksPage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    isbn: '',
    publication_year: new Date().getFullYear(),
    total_copies: 1,
  });

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    try {
      const response = await fetch('/api/books');
      const data = await response.json();
      setBooks(data);
    } catch (error) {
      console.error('Error fetching books:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/books', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });
      
      if (response.ok) {
        await fetchBooks();
        setShowForm(false);
        setFormData({
          title: '',
          author: '',
          isbn: '',
          publication_year: new Date().getFullYear(),
          total_copies: 1,
        });
      }
    } catch (error) {
      console.error('Error creating book:', error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await fetch(`/api/books/${id}`, { method: 'DELETE' });
      await fetchBooks();
    } catch (error) {
      console.error('Error deleting book:', error);
    }
  };

  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="border-b border-border">
        <div className="max-w-7xl mx-auto px-6 py-6">
          <Link href="/">
            <Button variant="ghost" className="mb-4">← Back</Button>
          </Link>
          <h1 className="text-3xl font-serif font-bold text-primary">Books</h1>
          <p className="text-muted-foreground mt-2">Manage library inventory</p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-serif font-bold">Book Collection</h2>
          <Button 
            onClick={() => setShowForm(!showForm)}
            className="bg-primary text-primary-foreground hover:bg-accent"
          >
            {showForm ? 'Cancel' : '+ Add Book'}
          </Button>
        </div>

        {showForm && (
          <Card className="bg-card border-border mb-6">
            <CardHeader>
              <CardTitle>Add New Book</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid md:grid-cols-2 gap-4">
                  <Input
                    placeholder="Title"
                    value={formData.title}
                    onChange={(e) => setFormData({...formData, title: e.target.value})}
                    className="bg-secondary text-foreground border-border"
                    required
                  />
                  <Input
                    placeholder="Author"
                    value={formData.author}
                    onChange={(e) => setFormData({...formData, author: e.target.value})}
                    className="bg-secondary text-foreground border-border"
                    required
                  />
                  <Input
                    placeholder="ISBN"
                    value={formData.isbn}
                    onChange={(e) => setFormData({...formData, isbn: e.target.value})}
                    className="bg-secondary text-foreground border-border"
                    required
                  />
                  <Input
                    placeholder="Publication Year"
                    type="number"
                    value={formData.publication_year}
                    onChange={(e) => setFormData({...formData, publication_year: parseInt(e.target.value)})}
                    className="bg-secondary text-foreground border-border"
                    required
                  />
                  <Input
                    placeholder="Total Copies"
                    type="number"
                    value={formData.total_copies}
                    onChange={(e) => setFormData({...formData, total_copies: parseInt(e.target.value)})}
                    className="bg-secondary text-foreground border-border"
                    required
                  />
                </div>
                <Button 
                  type="submit"
                  className="bg-primary text-primary-foreground hover:bg-accent"
                >
                  Create Book
                </Button>
              </form>
            </CardContent>
          </Card>
        )}

        <Card className="bg-card border-border">
          <CardContent className="pt-6">
            {isLoading ? (
              <div className="text-center py-8 text-muted-foreground">Loading books...</div>
            ) : books.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">No books found</div>
            ) : (
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow className="border-border hover:bg-secondary">
                      <TableHead className="text-foreground font-mono">Title</TableHead>
                      <TableHead className="text-foreground font-mono">Author</TableHead>
                      <TableHead className="text-foreground font-mono">ISBN</TableHead>
                      <TableHead className="text-foreground font-mono">Year</TableHead>
                      <TableHead className="text-foreground font-mono">Total</TableHead>
                      <TableHead className="text-foreground font-mono">Available</TableHead>
                      <TableHead className="text-foreground font-mono">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {books.map((book) => (
                      <TableRow key={book.id} className="border-border hover:bg-secondary">
                        <TableCell className="font-serif">{book.title}</TableCell>
                        <TableCell className="font-serif">{book.author}</TableCell>
                        <TableCell className="font-mono text-muted-foreground">{book.isbn}</TableCell>
                        <TableCell className="font-mono">{book.publication_year}</TableCell>
                        <TableCell className="font-mono text-primary">{book.total_copies}</TableCell>
                        <TableCell className="font-mono text-primary">{book.available_copies}</TableCell>
                        <TableCell>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleDelete(book.id)}
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
