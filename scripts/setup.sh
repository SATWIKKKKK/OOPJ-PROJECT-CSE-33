#!/bin/bash

# Library Management System - Setup Script

echo "🏛️ Library Management System - Setup"
echo "===================================="
echo ""

# Check for .env.local file
if [ ! -f ".env.local" ]; then
    echo "❌ Error: .env.local file not found"
    echo "Please copy .env.example to .env.local and configure DATABASE_URL"
    exit 1
fi

# Check DATABASE_URL is set
if [ -z "$DATABASE_URL" ]; then
    echo "⚠️ Loading DATABASE_URL from .env.local..."
    source .env.local
fi

if [ -z "$DATABASE_URL" ]; then
    echo "❌ Error: DATABASE_URL not set in .env.local"
    exit 1
fi

echo "✅ Environment configured"
echo "📦 Installing dependencies..."
pnpm install

echo "🗄️ Setting up database..."
echo "  - Creating tables..."
# Tables will be created on first request
echo "  - Database setup will run on first app start"

echo ""
echo "✅ Setup complete!"
echo ""
echo "Next steps:"
echo "1. Start the development server: pnpm run dev"
echo "2. Visit http://localhost:3000"
echo "3. Click 'Setup' on first visit to initialize sample data"
echo ""
