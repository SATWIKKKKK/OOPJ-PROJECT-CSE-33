# GitHub Setup Instructions

Follow these steps to push the Library Management System to your GitHub repository.

## Prerequisites

- GitHub account
- Git installed locally
- Repository created on GitHub

## Steps

### 1. Create a New Repository on GitHub

1. Go to [github.com/new](https://github.com/new)
2. Repository name: `library-management`
3. Description: `Comprehensive library management and inventory system`
4. Choose Public or Private
5. Do NOT initialize with README (we have one)
6. Click "Create repository"

### 2. Initialize Git Locally

```bash
cd /path/to/library-management

# Initialize git repository
git init

# Add all files
git add .

# Create initial commit
git commit -m "Initial commit: Library Management System

- Full-stack application with Next.js and PostgreSQL
- Complete CRUD operations for books, members, librarians, and issues
- 14+ analytical queries and database views
- Dark editorial theme with Playfair Display typography
- Ready for deployment to Vercel"

# Add remote repository (replace USERNAME and REPO)
git remote add origin https://github.com/USERNAME/library-management.git

# Create main branch and push
git branch -M main
git push -u origin main
```

### 3. Verify Push

Visit your GitHub repository URL to confirm all files are there:
```
https://github.com/USERNAME/library-management
```

## Subsequent Updates

After initial setup, use standard git workflow:

```bash
# Make changes
git add .
git commit -m "Description of changes"
git push origin main
```

## Deploy to Vercel (Optional)

1. Visit [vercel.com/new](https://vercel.com/new)
2. Connect your GitHub repository
3. Configure environment variables:
   - `DATABASE_URL`: Your Neon PostgreSQL connection string
4. Deploy!

## Branches

Recommended branch strategy:

```bash
# Create feature branch
git checkout -b feature/your-feature

# Make changes and commit
git add .
git commit -m "Add feature description"

# Push to GitHub
git push origin feature/your-feature

# Create Pull Request on GitHub
# Merge to main when ready
```

## Common Commands

```bash
# Check status
git status

# View commit history
git log --oneline

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Undo last commit (discard changes)
git reset --hard HEAD~1

# Switch branches
git checkout branch-name

# Delete branch
git branch -d branch-name
```

## Troubleshooting

### "fatal: not a git repository"
```bash
git init
```

### "remote: Permission denied"
- Check your GitHub credentials
- Generate and use a Personal Access Token
- Or use SSH keys

### "Can't push to main"
```bash
# Create main branch first
git branch -M main
# Then push
git push -u origin main
```

## Need Help?

- Git Documentation: https://git-scm.com/doc
- GitHub Help: https://help.github.com
- Vercel Deployment: https://vercel.com/docs
