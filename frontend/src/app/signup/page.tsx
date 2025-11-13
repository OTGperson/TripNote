"use client";

import { useEffect, useState, ChangeEvent, FormEvent } from "react";

type Member = {
  id: number;
  email: string;
  username: string;
  nickname: string;
  role: string;
};

type SignupForm = {
  email: string;
  username: string;
  password1: string;
  password2: string;
  nickname: string;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export default function SignupPage() {
  const [members, setMembers] = useState<Member[]>([]);
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

  const fetchMembers = async () => {
    if (!API_BASE_URL) {
      setError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }

    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/member/signup`);
      if (!res.ok) throw new Error();
      const data: Member[] = await res.json();
      setMembers(data);
    } catch {
      setError("회원 목록 조회 중 오류가 발생했습니다.");
    }
  };

  useEffect(() => {
    fetchMembers();
  }, []);

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

      if (!res.ok) throw new Error();

      const created: Member = await res.json();
      setMessage(`회원가입 성공! 환영합니다, ${created.username}님.`);
      setForm({
        email: "",
        username: "",
        password1: "",
        password2: "",
        nickname: "",
      });
      fetchMembers();
    } catch {
      setError("회원가입 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <h1>회원가입</h1>

      <form
        onSubmit={handleSubmit}
        style={{
          display: "flex",
          flexDirection: "column",
          gap: "8px",
          maxWidth: 320,
        }}
      >
        <label>
          이메일
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          아이디
          <input
            type="text"
            name="username"
            value={form.username}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          닉네임
          <input
            type="text"
            name="nickname"
            value={form.nickname}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          비밀번호
          <input
            type="password"
            name="password1"
            value={form.password1}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          비밀번호 확인
          <input
            type="password"
            name="password2"
            value={form.password2}
            onChange={handleChange}
            required
          />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? "처리 중..." : "회원가입"}
        </button>
      </form>

      {message && <p style={{ color: "green", marginTop: "8px" }}>{message}</p>}
      {error && <p style={{ color: "red", marginTop: "8px" }}>{error}</p>}

      <hr style={{ margin: "16px 0" }} />

      <h2>현재 회원 목록</h2>
      <ul>
        {members.map((member) => (
          <li key={member.id}>
            {member.username} ({member.nickname}) - {member.email} [
            {member.role}]
          </li>
        ))}
      </ul>
    </>
  );
}
