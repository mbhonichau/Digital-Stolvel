import React from 'react';
import { Link } from 'react-router-dom';
import { Home, Compass } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <main className="flex-1 flex flex-col items-center justify-center p-6 text-center bg-mtn-base text-mtn-cream">
      <div className="w-16 h-16 bg-mtn-surface border border-mtn-border rounded-full flex items-center justify-center text-mtn-cream-secondary mb-4">
        <Compass className="w-8 h-8 text-mtn-gold" />
      </div>
      <h1 className="text-4xl font-extrabold text-mtn-cream mb-2">404</h1>
      <h2 className="text-lg font-bold text-mtn-cream-secondary mb-2">Page Not Found</h2>
      <p className="text-sm text-mtn-cream-muted max-w-sm mb-6">
        The page you are looking for does not exist or has been moved.
      </p>
      <Link
        to="/"
        className="inline-flex items-center gap-2 px-4 py-2 bg-mtn-gold text-mtn-base font-bold rounded-lg hover:bg-mtn-gold-hover transition-colors shadow-sm text-sm focus:outline-none focus:ring-2 focus:ring-mtn-gold"
      >
        <Home className="w-4 h-4" />
        Return Home
      </Link>
    </main>
  );
};

export default NotFoundPage;
