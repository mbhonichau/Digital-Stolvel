import React from 'react';

export type HexAvatarSize = 'sm' | 'md' | 'lg' | 'xl';
export type HexAvatarStatus = 'online' | 'offline' | 'success' | 'failure' | 'none';

export interface HexAvatarProps {
  name?: string;
  src?: string;
  size?: HexAvatarSize;
  status?: HexAvatarStatus;
  className?: string;
}

export const HexAvatar: React.FC<HexAvatarProps> = ({
  name = '',
  src,
  size = 'md',
  status = 'none',
  className = '',
}) => {
  const sizeMap: Record<HexAvatarSize, { width: number; height: number; font: string; ring: string }> = {
    sm: { width: 32, height: 36, font: 'text-xs', ring: 'w-2 h-2 -bottom-0.5 -right-0.5' },
    md: { width: 44, height: 50, font: 'text-sm font-bold', ring: 'w-2.5 h-2.5 bottom-0 right-0' },
    lg: { width: 60, height: 68, font: 'text-base font-extrabold', ring: 'w-3 h-3 bottom-0.5 right-0.5' },
    xl: { width: 80, height: 90, font: 'text-xl font-black', ring: 'w-3.5 h-3.5 bottom-1 right-1' },
  };

  const currentSize = sizeMap[size];

  const getInitials = (text: string) => {
    if (!text) return '?';
    const parts = text.trim().split(/\s+/);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return text.substring(0, 2).toUpperCase();
  };

  const statusColorMap: Record<HexAvatarStatus, string> = {
    online: 'bg-mtn-green border-mtn-base',
    offline: 'bg-mtn-cream-muted border-mtn-base',
    success: 'bg-mtn-green border-mtn-base',
    failure: 'bg-mtn-red border-mtn-base',
    none: '',
  };

  // SVG Hexagon points viewBox="0 0 100 115.47"
  return (
    <div className={`relative inline-flex items-center justify-center shrink-0 ${className}`}>
      <svg
        width={currentSize.width}
        height={currentSize.height}
        viewBox="0 0 100 115.47"
        className="drop-shadow-sm overflow-visible"
      >
        <defs>
          <clipPath id={`hex-clip-${size}-${name.replace(/\s+/g, '')}`}>
            <polygon points="50 0, 100 28.87, 100 86.6, 50 115.47, 0 86.6, 0 28.87" />
          </clipPath>
        </defs>

        {/* Outer border hexagon */}
        <polygon
          points="50 0, 100 28.87, 100 86.6, 50 115.47, 0 86.6, 0 28.87"
          className="fill-mtn-surface stroke-mtn-gold stroke-[4]"
        />

        {/* Inner Content Area */}
        <g clipPath={`url(#hex-clip-${size}-${name.replace(/\s+/g, '')})`}>
          {src ? (
            <image href={src} width="100" height="115.47" preserveAspectRatio="xMidYMid slice" />
          ) : (
            <>
              <rect width="100" height="115.47" className="fill-mtn-surface" />
              <text
                x="50%"
                y="55%"
                dominantBaseline="middle"
                textAnchor="middle"
                className={`fill-mtn-gold font-extrabold ${currentSize.font} select-none`}
              >
                {getInitials(name)}
              </text>
            </>
          )}
        </g>
      </svg>

      {/* Optional Status Indicator Badge */}
      {status !== 'none' && (
        <span
          className={`absolute rounded-full border-2 ${statusColorMap[status]} ${currentSize.ring}`}
        />
      )}
    </div>
  );
};

export default HexAvatar;
