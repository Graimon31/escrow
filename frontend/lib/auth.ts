export type LoginResponse = {
  accessToken: string;
  tokenType: string;
  role: 'DEPOSITOR' | 'BENEFICIARY' | 'OPERATOR' | 'ADMIN';
  displayName: string;
};

const authBase = process.env.NEXT_PUBLIC_AUTH_SERVICE_URL || 'http://localhost:8081';

export async function login(username: string, password: string): Promise<LoginResponse> {
  const response = await fetch(`${authBase}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });

  if (!response.ok) {
    throw new Error('Неверный логин или пароль');
  }

  return response.json();
}

export async function me(token: string): Promise<{ username: string; role: string; displayName: string }> {
  const response = await fetch(`${authBase}/api/v1/auth/me`, {
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!response.ok) {
    throw new Error('Сессия истекла');
  }

  return response.json();
}
