"use client";

import { useState, ChangeEvent, FormEvent } from "react";

type SignupForm = {
  email: string;
  username: string;
  password1: string;
  password2: string;
  nickname: string;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export default function SignupPage() {
  const [form, setForm] = useState<SignupForm>({
    email: "",
    username: "",
    password1: "",
    password2: "",
    nickname: "",
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setMessage(null);
    setError(null);

    if (!API_BASE_URL) {
      setError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }

    if (form.password1 !== form.password2) {
      setError("비밀번호가 일치하지 않습니다.");
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/member/signup`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      if (!res.ok) {
        throw new Error();
      }

      const created = await res.json();
      setMessage(`회원가입 성공! 환영합니다, ${created.username}님.`);
      setForm({
        email: "",
        username: "",
        password1: "",
        password2: "",
        nickname: "",
      });
    } catch {
      setError("회원가입 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="signup-page">
      <div className="signup-card">
        <header className="signup-header">
          <h1>TripNote 회원가입</h1>
          <p>여행 기록을 시작해보세요.</p>
        </header>

        <form className="signup-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">이메일</label>
            <input
              id="email"
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
              placeholder="example@tripnote.com"
            />
          </div>

          <div className="form-group">
            <label htmlFor="username">아이디</label>
            <input
              id="username"
              type="text"
              name="username"
              value={form.username}
              onChange={handleChange}
              required
              placeholder="영문/숫자 조합"
            />
          </div>

          <div className="form-group">
            <label htmlFor="nickname">닉네임</label>
            <input
              id="nickname"
              type="text"
              name="nickname"
              value={form.nickname}
              onChange={handleChange}
              required
              placeholder="서비스에서 보여질 이름"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password1">비밀번호</label>
            <input
              id="password1"
              type="password"
              name="password1"
              value={form.password1}
              onChange={handleChange}
              required
              placeholder="8자 이상 입력"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password2">비밀번호 확인</label>
            <input
              id="password2"
              type="password"
              name="password2"
              value={form.password2}
              onChange={handleChange}
              required
              placeholder="비밀번호를 한 번 더 입력"
            />
          </div>

          {error && <p className="signup-message error">{error}</p>}
          {message && <p className="signup-message success">{message}</p>}

          <button type="submit" className="signup-button" disabled={loading}>
            {loading ? "처리 중..." : "가입하기"}
          </button>
        </form>

        <footer className="signup-footer">
          <span>이미 계정이 있으신가요?</span>
          <a href="/login">로그인 하러 가기</a>
        </footer>
      </div>
    </main>
  );
}
