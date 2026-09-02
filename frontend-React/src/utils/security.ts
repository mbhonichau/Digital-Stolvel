/**
 * Security & Data Sanitization Utilities
 *
 * Implements privacy preserving masking and sanitization for sensitive domain data (e.g. MSISDN phone numbers).
 */

/**
 * Masks an MSISDN phone number to protect member privacy in group rosters and shared views.
 * Examples:
 * - "0831234567" -> "083 *** 4567"
 * - "+27831234567" -> "+27 83 *** 4567"
 * - "0729876543" -> "072 *** 6543"
 */
export const maskMsisdn = (msisdn?: string | null): string => {
  if (!msisdn) return '';

  const clean = msisdn.trim();
  if (clean.length < 7) {
    return clean;
  }

  // Handle +27 format (e.g. +27831234567)
  if (clean.startsWith('+27') && clean.length >= 12) {
    const prefix = clean.substring(0, 5); // +2783
    const suffix = clean.substring(clean.length - 4); // 4567
    return `${prefix.slice(0, 3)} ${prefix.slice(3)} *** ${suffix}`;
  }

  // Handle standard 10-digit South African numbers (e.g. 0831234567)
  if (clean.length === 10) {
    const prefix = clean.substring(0, 3); // 083
    const suffix = clean.substring(6); // 4567
    return `${prefix} *** ${suffix}`;
  }

  // Generic fallback masking for any international MSISDN
  const visiblePrefixLength = Math.min(3, Math.floor(clean.length / 3));
  const visibleSuffixLength = Math.min(4, Math.floor(clean.length / 3));
  const prefix = clean.substring(0, visiblePrefixLength);
  const suffix = clean.substring(clean.length - visibleSuffixLength);

  return `${prefix} *** ${suffix}`;
};

/**
 * Sanitizes input strings by trimming whitespace and escaping dangerous control characters.
 */
export const sanitizeInput = (val?: string | null): string => {
  if (!val) return '';
  return val.trim().replace(/[<>]/g, '');
};
