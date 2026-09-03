import React from 'react';

export interface HexBackgroundProps {
  className?: string;
  glowIntensity?: 'subtle' | 'medium' | 'none';
}

/**
 * Geometric hexagonal background constructed strictly with SVG and CSS.
 * Zero raster images used. Subtly styled for Mzansi Futurism aesthetic.
 */
export const HexBackground: React.FC<HexBackgroundProps> = ({
  className = '',
  glowIntensity = 'subtle',
}) => {
  return (
    <div
      className={`fixed inset-0 pointer-events-none overflow-hidden select-none z-0 bg-white ${className}`}
      aria-hidden="true"
    >
      {/* Ambient Radial Gradient Glows */}
      {glowIntensity !== 'none' && (
        <>
          <div className="absolute -top-24 -right-24 w-96 h-96 rounded-full bg-mtn-gold opacity-[0.10] blur-[100px]" />
        </>
      )}

      {/* Repeating Pure SVG Hexagonal Mesh Pattern */}
      <svg
        className="absolute inset-0 w-full h-full opacity-[0.28]"
        xmlns="http://www.w3.org/2000/svg"
        width="100%"
        height="100%"
      >
        <defs>
          <pattern
            id="mzansi-hex-grid"
            width="56"
            height="96.994"
            patternUnits="userSpaceOnUse"
            patternTransform="scale(1)"
          >
            {/* Hexagon 1 */}
            <path
              d="M28 0 L56 16.165 L56 48.497 L28 64.662 L0 48.497 L0 16.165 Z"
              fill="none"
              stroke="#B8C9D1"
              strokeWidth="0.75"
              strokeOpacity="0.4"
            />
            {/* Hexagon 2 (Offset row) */}
            <path
              d="M28 64.662 L56 80.827 L56 113.159 L28 129.324 L0 113.159 L0 80.827 Z"
              fill="none"
              stroke="#B8C9D1"
              strokeWidth="0.75"
              strokeOpacity="0.4"
            />
            {/* Offset Interlocking Hexagons */}
            <path
              d="M56 32.331 L84 48.496 L84 80.828 L56 96.993 L28 80.828 L28 48.496 Z"
              fill="none"
              stroke="#E6B705"
              strokeWidth="0.75"
              strokeOpacity="0.3"
            />
            <path
              d="M0 32.331 L28 48.496 L28 80.828 L0 96.993 L-28 80.828 L-28 48.496 Z"
              fill="none"
              stroke="#E6B705"
              strokeWidth="0.75"
              strokeOpacity="0.3"
            />
          </pattern>
        </defs>

        <rect width="100%" height="100%" fill="url(#mzansi-hex-grid)" />
      </svg>
    </div>
  );
};

export default HexBackground;
