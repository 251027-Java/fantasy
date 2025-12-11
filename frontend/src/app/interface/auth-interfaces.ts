export interface AuthResponse {
    email: string;
    name: string;
    jwtToken: string;
}

export interface AuthRequest {
    code: string;
}

