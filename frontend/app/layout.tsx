import './globals.css';
import type { ReactNode } from 'react';

export const metadata = {
  title: 'Платформа эскроу (Escrow Platform)',
  description: 'Каркас фронтенда на Next.js + Tailwind CSS'
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="ru">
      <body>{children}</body>
    </html>
  );
}
