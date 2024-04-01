

export interface HeaderProps {
    clearSession: () => Promise<void>;
    name?: string;
    history?: Record<string, string>;
}