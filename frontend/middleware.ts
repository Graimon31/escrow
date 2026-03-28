import { NextRequest, NextResponse } from 'next/server';

function getRole(request: NextRequest): string | undefined {
  return request.cookies.get('auth_role')?.value;
}

function hasToken(request: NextRequest): boolean {
  return Boolean(request.cookies.get('auth_token')?.value);
}

export function middleware(request: NextRequest) {
  const path = request.nextUrl.pathname;

  if (path.startsWith('/cabinet') || path.startsWith('/deals')) {
    if (!hasToken(request)) {
      return NextResponse.redirect(new URL('/login', request.url));
    }
  }

  if (path.startsWith('/cabinet')) {
    const role = getRole(request);

    if (path.startsWith('/cabinet/admin') && role !== 'ADMIN') {
      return NextResponse.redirect(new URL('/cabinet', request.url));
    }
    if (path.startsWith('/cabinet/operator') && role !== 'OPERATOR' && role !== 'ADMIN') {
      return NextResponse.redirect(new URL('/cabinet', request.url));
    }
    if (path.startsWith('/cabinet/depositor') && role !== 'DEPOSITOR') {
      return NextResponse.redirect(new URL('/cabinet', request.url));
    }
    if (path.startsWith('/cabinet/beneficiary') && role !== 'BENEFICIARY') {
      return NextResponse.redirect(new URL('/cabinet', request.url));
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/cabinet/:path*', '/deals/:path*']
};
