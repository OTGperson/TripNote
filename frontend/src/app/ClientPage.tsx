"use client";

import Link from "next/link";

export default function ClientPage() {
  return (
    <main style={{ padding: 32 }}>
      <h1>메인 화면</h1>
      <p>여기는 메인 페이지입니다.</p>

      <Link href="/signup">
        <button style={{ marginTop: 16 }}>회원가입 하러 가기</button>
      </Link>
    </main>
  );
}
