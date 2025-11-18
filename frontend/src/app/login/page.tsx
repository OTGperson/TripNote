"use client";

import { useState, ChangeEvent, FormEvent } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";

type LoginForm = {
  username: string;
  password: string;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export default function LoginPage() {
  const router = useRouter();

  const [form, setForm] = useState<LoginForm>({
    username: "",
    password: "",
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

    setLoading(true);

    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/member/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      const data = await res.json().catch(() => ({}));

      // success 플래그가 있다고 가정
      if (!res.ok || data.success === false) {
        const msg =
          data?.message || "아이디 또는 비밀번호가 일치하지 않습니다.";
        setError(msg);
        return;
      }

      // TODO: 나중에 여기에서 토큰/유저정보를 저장하거나 상태관리로 넘기면 됨
      setMessage(
        `로그인 성공! 환영합니다, ${data.username ?? form.username}님.`
      );

      // 잠깐 보여주고 메인으로 이동 (원하면 주석 풀기)
      setTimeout(() => {
        router.push("/");
      }, 800);
    } catch (err) {
      console.error(err);
      setError("로그인 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="signup-page">
      <div className="signup-card">
        <header className="signup-header">
          <h1>로그인</h1>
          <p>TripNote에 다시 오신 걸 환영해요.</p>
        </header>

        <form className="signup-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">아이디</label>
            <input
              id="username"
              type="text"
              name="username"
              value={form.username}
              onChange={handleChange}
              required
              placeholder="가입하신 아이디를 입력하세요"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
              placeholder="비밀번호를 입력하세요"
            />
          </div>

          {error && <p className="signup-message error">{error}</p>}
          {message && <p className="signup-message success">{message}</p>}

          <button type="submit" className="signup-button" disabled={loading}>
            {loading ? "확인 중..." : "로그인"}
          </button>
        </form>

        <footer className="signup-footer">
          <span>아직 계정이 없으신가요?</span>
          <Link href="/signup">회원가입 하러 가기</Link>
        </footer>
      </div>
    </main>
  );
}
