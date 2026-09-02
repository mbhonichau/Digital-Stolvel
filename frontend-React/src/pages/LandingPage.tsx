import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button, HexBadge } from '@/components';
import { Sparkles, PlusCircle, UserPlus, Shield, Coins, Repeat, ArrowRight } from 'lucide-react';

export const LandingPage: React.FC = () => {
  const navigate = useNavigate();

  const valueProps = [
    {
      icon: Coins,
      title: 'Automated MoMo Savings',
      description: 'Collect contributions securely and seamlessly through MTN Mobile Money.',
    },
    {
      icon: Repeat,
      title: 'Fair Cycle Rotations',
      description: 'Scheduled payout disbursements directly to members with total transparency.',
    },
    {
      icon: Shield,
      title: 'Full Audit Trail',
      description: 'Real-time transaction statuses with zero manual spreadsheets or cash handling.',
    },
  ];

  return (
    <div className="w-full space-y-6 animate-fade-in">
      {/* Hero Card */}
      <Card variant="elevated" glow="gold" className="p-6 md:p-8 relative overflow-hidden text-center">
        <div className="absolute -top-12 -right-12 w-40 h-40 bg-mtn-gold-muted rounded-full blur-3xl pointer-events-none" />

        <div className="inline-flex items-center gap-1.5 mb-4">
          <HexBadge variant="gold" size="sm" icon={<Sparkles className="w-3 h-3 text-mtn-gold" />}>
            MTN MoMo Stokvel
          </HexBadge>
        </div>

        <h1 className="text-3xl md:text-4xl font-black text-mtn-cream tracking-tight leading-tight mb-3">
          Smarter Stokvels.<br />
          <span className="text-mtn-gold">Instant MoMo Payouts.</span>
        </h1>

        <p className="text-sm text-mtn-cream-secondary max-w-sm mx-auto mb-6 leading-relaxed">
          Pool funds with family, friends, or colleagues. Automated contributions and guaranteed rotations powered by MTN MoMo.
        </p>

        {/* Primary Call-to-Actions */}
        <div className="flex flex-col sm:flex-row gap-3 max-w-sm mx-auto">
          <Button
            variant="primary"
            size="md"
            fullWidth
            onClick={() => navigate('/create')}
            leftIcon={<PlusCircle className="w-4 h-4" />}
            rightIcon={<ArrowRight className="w-4 h-4" />}
            label="Create Stokvel"
          />
          <Button
            variant="secondary"
            size="md"
            fullWidth
            onClick={() => navigate('/join')}
            leftIcon={<UserPlus className="w-4 h-4" />}
            label="Join with Code"
          />
        </div>
      </Card>

      {/* Value Proposition Highlights */}
      <div className="space-y-3">
        <div className="flex items-center justify-between px-1">
          <h2 className="text-xs font-bold text-mtn-cream-secondary uppercase tracking-wider">
            Why MoMo Stokvel?
          </h2>
          <span className="text-[10px] text-mtn-cream-muted font-mono">Mzansi Futurism</span>
        </div>

        <div className="grid gap-3">
          {valueProps.map((item, index) => {
            const Icon = item.icon;
            return (
              <Card
                key={index}
                variant="default"
                className="p-4 flex items-start gap-3.5 hover:border-mtn-gold/30 transition-colors"
              >
                <div className="w-10 h-10 rounded-xl bg-mtn-base border border-mtn-border flex items-center justify-center shrink-0 text-mtn-gold shadow-sm">
                  <Icon className="w-5 h-5" />
                </div>
                <div className="flex-1">
                  <h3 className="text-sm font-bold text-mtn-cream mb-0.5">{item.title}</h3>
                  <p className="text-xs text-mtn-cream-secondary leading-relaxed">{item.description}</p>
                </div>
              </Card>
            );
          })}
        </div>
      </div>

      {/* Security & Trust Footer Banner */}
      <Card variant="default" className="p-4 flex items-center gap-3 bg-mtn-surface/60 border-mtn-border/50">
        <div className="w-8 h-8 rounded-lg bg-mtn-green-muted border border-mtn-green/30 flex items-center justify-center shrink-0 text-mtn-green">
          <Shield className="w-4 h-4" />
        </div>
        <div className="flex-1 text-left">
          <p className="text-xs font-bold text-mtn-cream">Direct MTN MoMo Integration</p>
          <p className="text-[11px] text-mtn-cream-secondary">
            Funds and payouts execute through real mobile money channels.
          </p>
        </div>
      </Card>
    </div>
  );
};

export const Landing = LandingPage;
export default LandingPage;
