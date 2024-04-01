export interface Session {
    name?: string;
    exchanges: Exchange[];
    currentModel?: string;
    sessionIdsAndNames?: any;
}

export interface Exchange {
    request: string;
    response?: string;
    systemMessage?: string;
    id: string;
}