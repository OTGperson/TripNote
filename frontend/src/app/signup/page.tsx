"use client";

import { useState, ChangeEvent, FormEvent } from "react";
import { useRouter } from "next/navigation";

type SignupForm = {
  email: string;
  username: string;
  password1: string;
  password2: string;
  nickname: string;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export default function SignupPage() {
  const router = useRouter();

  const [form, setForm] = useState<SignupForm>({
    email: "",
    username: "",
    password1: "",
    password2: "",
    nickname: "",
  });

  // 🔹 이메일 인증 관련 상태
  const [emailCode, setEmailCode] = useState("");
  const [emailSending, setEmailSending] = useState(false);
  const [emailVerifying, setEmailVerifying] = useState(false);
  const [emailVerified, setEmailVerified] = useState(false);
  const [emailMessage, setEmailMessage] = useState<string | null>(null);
  const [emailError, setEmailError] = useState<string | null>(null);

  // 🔹 전체 회원가입 관련 상태
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));

    // 이메일이 바뀌면 인증 상태 초기화
    if (name === "email") {
      setEmailVerified(false);
      setEmailCode("");
      setEmailMessage(null);
      setEmailError(null);
    }
  };

  // 🔹 1) 이메일 인증 코드 보내기
  const handleSendEmailCode = async () => {
    setEmailMessage(null);
    setEmailError(null);

    if (!API_BASE_URL) {
      setEmailError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }

    if (!form.email) {
      setEmailError("이메일을 먼저 입력해주세요.");
      return;
    }

    setEmailSending(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/member/email/send-code`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: form.email }),
      });

      const data = await res.json().catch(() => ({}));

      // 🔹 이미 가입된 이메일인 경우
      if (data.alreadyRegistered) {
        const goLogin = window.confirm(
          "이미 가입된 이메일입니다.\n로그인 하러 가시겠습니까?"
        );
        if (goLogin) {
          router.push("/login");
        }
        return;
      }

      if (!res.ok || data.success === false) {
        setEmailError(data.message ?? "인증 메일 전송 중 오류가 발생했습니다.");
        return;
      }

      setEmailMessage("인증 코드를 이메일로 전송했습니다.");
    } catch {
      setEmailError("인증 메일 전송 중 오류가 발생했습니다.");
    } finally {
      setEmailSending(false);
    }
  };

  // 🔹 2) 인증 코드 검증
  const handleVerifyEmailCode = async () => {
    setEmailMessage(null);
    setEmailError(null);

    if (!API_BASE_URL) {
      setEmailError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }

    if (!form.email) {
      setEmailError("이메일을 먼저 입력해주세요.");
      return;
    }

    if (!emailCode) {
      setEmailError("인증 코드를 입력해주세요.");
      return;
    }

    setEmailVerifying(true);
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/member/email/check-code`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email: form.email, code: emailCode }),
        }
      );

      const data = await res.json().catch(() => ({}));

      if (!res.ok || data.success === false) {
        setEmailVerified(false);
        setEmailError(
          data.message ?? "인증번호가 올바르지 않거나 만료되었습니다."
        );
        return;
      }

      setEmailVerified(true);
      setEmailMessage("이메일 인증이 완료되었습니다.");
    } catch {
      setEmailError("이메일 인증 중 오류가 발생했습니다.");
    } finally {
      setEmailVerifying(false);
    }
  };

  // 🔹 3) 최종 회원가입
  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setMessage(null);
    setError(null);

    if (!API_BASE_URL) {
      setError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }

    if (!emailVerified) {
      setError("이메일 인증을 완료해주세요.");
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

      const data = await res.json().catch(() => ({}));

      if (!res.ok || data.success === false) {
        setError(data.message ?? "회원가입 중 오류가 발생했습니다.");
        return;
      }

      // ✅ 가입 성공 → 팝업 + /login 으로 이동
      alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
      router.push("/login");
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
          {/* 이메일 + 인증 버튼 */}
          <div className="form-group">
            <label htmlFor="email">이메일</label>
            <div style={{ display: "flex", gap: "8px" }}>
              <input
                id="email"
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                required
                placeholder="example@tripnote.com"
                style={{ flex: 1 }}
                disabled={emailVerified}
              />
              <button
                type="button"
                onClick={handleSendEmailCode}
                disabled={emailSending || !form.email || emailVerified}
                className="signup-button"
                style={{ width: "120px", marginTop: 0 }}
              >
                {emailSending
                  ? "전송 중..."
                  : emailVerified
                  ? "인증 완료"
                  : "인증"}
              </button>
            </div>
          </div>

          {/* 인증 코드 + 확인 버튼 */}
          <div className="form-group">
            <label htmlFor="emailCode">인증 코드</label>
            <div style={{ display: "flex", gap: "8px" }}>
              <input
                id="emailCode"
                type="text"
                name="emailCode"
                value={emailCode}
                onChange={(e) => setEmailCode(e.target.value)}
                placeholder="메일로 받은 6자리 코드를 입력"
                style={{ flex: 1 }}
                disabled={emailVerified}
              />
              <button
                type="button"
                onClick={handleVerifyEmailCode}
                disabled={emailVerifying || !emailCode || emailVerified}
                className="signup-button"
                style={{ width: "120px", marginTop: 0 }}
              >
                {emailVerifying ? "확인 중..." : "확인"}
              </button>
            </div>
          </div>

          {/* 이메일 인증 관련 메시지 */}
          {emailError && <p className="signup-message error">{emailError}</p>}
          {emailMessage && (
            <p className="signup-message success">{emailMessage}</p>
          )}

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
          <div style={{ display: "flex", gap: "6px", alignItems: "center" }}>
            <a href="/login">로그인 하러 가기</a>
            <span>/ </span>
            <a
              href="/"
              type="button"
              onClick={() => router.push("/")}
              style={{
                border: "none",
                background: "none",
                padding: 0,
                margin: 0,
                color: "#3b82f6",
                fontWeight: 600,
                cursor: "pointer",
              }}
            >
              비회원으로 계속하기
            </a>
          </div>
        </footer>
      </div>
    </main>
  );
}
