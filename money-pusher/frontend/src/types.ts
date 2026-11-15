export type AuthTokens = { access_token: string; refresh_token: string };
export type User = { nickname: string; email: string; role: 'USER' | 'ADMIN' };
export type Balance = { user_balance: number };
export type LeaderboardItem = { balance: number; user_nickname: string; user_email: string };
export type TransferStatus = 'PENDING' | 'SUCCESS' | 'FAILED';
export type Transfer = {
  transfer_id: number; transfer_amount: number; initiator_email: string; target_email: string;
  additional_message?: string | null; status: TransferStatus; created_at?: string; createdAt?: string;
};
export type ApiError = Error & { status?: number; category?: string };
