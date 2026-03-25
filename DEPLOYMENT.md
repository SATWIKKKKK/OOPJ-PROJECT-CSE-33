# Deployment Guide

This guide covers deploying the Library Management System to production environments.

## Vercel Deployment (Recommended)

Vercel is the optimal choice for Next.js applications.

### Prerequisites

- Vercel account (free at vercel.com)
- GitHub repository pushed
- Neon PostgreSQL database

### Step 1: Connect GitHub

1. Go to [vercel.com/new](https://vercel.com/new)
2. Click "Import Git Repository"
3. Paste your GitHub repository URL
4. Click "Continue"

### Step 2: Configure Project

1. **Project Name**: `library-management` (or your choice)
2. **Framework Preset**: Next.js (auto-detected)
3. **Root Directory**: Leave as `.` (default)

### Step 3: Environment Variables

Click "Environment Variables" and add:

```
DATABASE_URL=postgresql://user:password@host:5432/library_db
NODE_ENV=production
```

Get your Neon database URL:
1. Go to neon.tech dashboard
2. Select your project
3. Copy connection string
4. Format: `postgresql://user:password@host/database`

### Step 4: Deploy

1. Click "Deploy"
2. Wait for build to complete
3. Visit your deployment URL

Your app is now live!

## Database Setup

### Using Neon (Serverless PostgreSQL)

1. **Create Account**: https://neon.tech
2. **Create Project**: 
   - Region: Select closest to your users
   - Database: `library_db`
   - User: `postgres`
3. **Copy Connection String**:
   - Format: `postgresql://user:password@host/database`
4. **Create Tables**:
   ```sql
   -- Copy contents of scripts/01-schema.sql
   -- Copy contents of scripts/02-views-triggers.sql
   ```
5. **Initialize Data**:
   - After deployment, visit `/api/setup`
   - Or use curl: `curl -X POST https://your-domain/api/setup`

### Using Local PostgreSQL

For development only:

```bash
# Install PostgreSQL
brew install postgresql  # macOS
# or
sudo apt-get install postgresql  # Linux

# Start service
brew services start postgresql
# or
sudo systemctl start postgresql

# Create database
createdb library_db

# Run migrations
psql library_db < scripts/01-schema.sql
psql library_db < scripts/02-views-triggers.sql

# Set DATABASE_URL
export DATABASE_URL=postgresql://localhost/library_db
```

## Domain Configuration

### Custom Domain (Vercel)

1. Go to Vercel project settings
2. Domains → Add
3. Enter your domain
4. Update DNS records per Vercel instructions
5. Wait for SSL certificate (24-48 hours)

### Using Subdomain

```
library.example.com → your-project.vercel.app
```

## Environment Variables

### Production

```env
DATABASE_URL=postgresql://user:pass@host/library_db
NODE_ENV=production
NEXT_PUBLIC_API_URL=https://your-domain.com
```

### Development

```env
DATABASE_URL=postgresql://localhost/library_db
NODE_ENV=development
NEXT_PUBLIC_API_URL=http://localhost:3000
```

### Staging

```env
DATABASE_URL=postgresql://user:pass@staging-host/library_db
NODE_ENV=production
NEXT_PUBLIC_API_URL=https://staging.your-domain.com
```

## Build Optimization

### Next.js Config

```javascript
// next.config.mjs
export default {
  productionBrowserSourceMaps: false,
  swcMinify: true,
  compress: true,
};
```

### Performance Monitoring

- Vercel Analytics: Enable in project settings
- Web Vitals: Already integrated
- Database Monitoring: Use Neon dashboard

## Security Best Practices

1. **Environment Variables**
   - Never commit `.env.local`
   - Use Vercel environment variables
   - Rotate secrets regularly

2. **Database**
   - Use strong passwords
   - Enable SSL for connections
   - Backup regularly

3. **CORS**
   - Configure for your domain
   - Restrict API access

4. **Rate Limiting**
   - Consider implementing per IP
   - Protect `/api/setup` endpoint

5. **HTTPS**
   - Always use HTTPS
   - Enable HSTS headers

## Monitoring & Logs

### Vercel Dashboard

- Deployments: History and status
- Logs: Real-time application logs
- Analytics: Performance metrics
- Edge Network: Request patterns

### Neon Dashboard

- Database metrics
- Query performance
- Connection limits
- Backup status

## Rollback

If deployment fails:

```bash
# Via Vercel Dashboard
1. Go to Deployments
2. Find previous successful deployment
3. Click "..." → "Redeploy"

# Via Git
git revert <commit-hash>
git push origin main
# Vercel auto-deploys
```

## Scaling

### Database Scaling

- Neon automatic scaling (Pro plan)
- Monitor CPU and connections
- Upgrade plan if needed

### Application Scaling

Vercel handles automatically:
- Auto-scaling functions
- CDN distribution
- Load balancing

## Maintenance

### Regular Tasks

- [ ] Check logs for errors
- [ ] Monitor database size
- [ ] Review analytics
- [ ] Update dependencies: `pnpm update`
- [ ] Test database backups

### Monthly

- [ ] Review usage metrics
- [ ] Check security updates
- [ ] Test disaster recovery
- [ ] Optimize slow queries

## Troubleshooting

### Build Fails

```bash
# Clear cache and redeploy
vercel --prod --yes --force

# Check logs for errors
vercel logs --follow
```

### Database Connection Error

1. Check `DATABASE_URL` format
2. Verify Neon service status
3. Check network connectivity
4. Review connection limits

### High Latency

1. Check database query performance
2. Review API response times
3. Monitor Neon CPU usage
4. Consider caching

## Support

- **Vercel**: https://vercel.com/help
- **Neon**: https://neon.tech/docs
- **Next.js**: https://nextjs.org/docs
- **PostgreSQL**: https://www.postgresql.org/docs/

## Disaster Recovery

### Backup Strategy

1. **Neon Backups**
   - Automatic daily backups
   - 7-day retention (free tier)
   - 30-day retention (Pro)

2. **Manual Backups**
   ```sql
   pg_dump -U postgres library_db > backup.sql
   ```

3. **Restore**
   ```sql
   psql library_db < backup.sql
   ```

### Incident Response

1. Immediate: Route traffic to backup
2. Investigation: Check logs and metrics
3. Fix: Deploy corrected code
4. Verify: Test functionality
5. Document: Update runbook
