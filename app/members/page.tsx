'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

interface Member {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  membership_date: string;
}

export default function MembersPage() {
  const [members, setMembers] = useState<Member[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    address: '',
  });

  useEffect(() => {
    fetchMembers();
  }, []);

  const fetchMembers = async () => {
    try {
      const response = await fetch('/api/members');
      const data = await response.json();
      setMembers(data);
    } catch (error) {
      console.error('Error fetching members:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/members', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });
      
      if (response.ok) {
        await fetchMembers();
        setShowForm(false);
        setFormData({ name: '', email: '', phone: '', address: '' });
      }
    } catch (error) {
      console.error('Error creating member:', error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await fetch(`/api/members/${id}`, { method: 'DELETE' });
      await fetchMembers();
    } catch (error) {
      console.error('Error deleting member:', error);
    }
  };

  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="border-b border-border">
        <div className="max-w-7xl mx-auto px-6 py-6">
          <Link href="/">
            <Button variant="ghost" className="mb-4">← Back</Button>
          </Link>
          <h1 className="text-3xl font-serif font-bold text-primary">Members</h1>
          <p className="text-muted-foreground mt-2">Manage library members</p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-serif font-bold">Member Directory</h2>
          <Button 
            onClick={() => setShowForm(!showForm)}
            className="bg-primary text-primary-foreground hover:bg-accent"
          >
            {showForm ? 'Cancel' : '+ Add Member'}
          </Button>
        </div>

        {showForm && (
          <Card className="bg-card border-border mb-6">
            <CardHeader>
              <CardTitle>Register New Member</CardTitle>
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
                  <Input
                    placeholder="Address"
                    value={formData.address}
                    onChange={(e) => setFormData({...formData, address: e.target.value})}
                    className="bg-secondary text-foreground border-border"
                  />
                </div>
                <Button 
                  type="submit"
                  className="bg-primary text-primary-foreground hover:bg-accent"
                >
                  Register Member
                </Button>
              </form>
            </CardContent>
          </Card>
        )}

        <Card className="bg-card border-border">
          <CardContent className="pt-6">
            {isLoading ? (
              <div className="text-center py-8 text-muted-foreground">Loading members...</div>
            ) : members.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">No members found</div>
            ) : (
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow className="border-border hover:bg-secondary">
                      <TableHead className="text-foreground font-mono">Name</TableHead>
                      <TableHead className="text-foreground font-mono">Email</TableHead>
                      <TableHead className="text-foreground font-mono">Phone</TableHead>
                      <TableHead className="text-foreground font-mono">Address</TableHead>
                      <TableHead className="text-foreground font-mono">Joined</TableHead>
                      <TableHead className="text-foreground font-mono">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {members.map((member) => (
                      <TableRow key={member.id} className="border-border hover:bg-secondary">
                        <TableCell className="font-serif">{member.name}</TableCell>
                        <TableCell className="font-mono text-muted-foreground">{member.email}</TableCell>
                        <TableCell className="font-mono">{member.phone}</TableCell>
                        <TableCell className="text-sm">{member.address}</TableCell>
                        <TableCell className="font-mono text-muted-foreground">{new Date(member.membership_date).toLocaleDateString()}</TableCell>
                        <TableCell>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleDelete(member.id)}
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
