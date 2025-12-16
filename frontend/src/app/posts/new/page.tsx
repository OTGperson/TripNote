"use client";

import { useState, FormEvent, ChangeEvent, useEffect } from "react";
import { useRouter } from "next/navigation";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export default function PostCreatePage() {
  const router = useRouter();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [isPublic, setIsPublic] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  // 로그인 안 되어 있으면 로그인 페이지로
  useEffect(() => {
    if (typeof window === "undefined") return;
    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("게시글 작성은 로그인 후 이용 가능합니다.");
      router.replace("/login");
    }
  }, [router]);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);

    if (!API_BASE_URL) {
      setError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }

    if (typeof window === "undefined") return;

    const token = localStorage.getItem("accessToken");
    if (!token) {
      setError("로그인이 필요합니다.");
      return;
    }

    if (!title.trim()) {
      setError("제목을 입력해 주세요.");
      return;
    }
    if (!content.trim()) {
      setError("내용을 입력해 주세요.");
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/post`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ title, content, isPublic }),
      });

      const data = await res.json().catch(() => ({}));

      if (!res.ok || data.success === false) {
        setError(data.message ?? "게시글 작성 중 오류가 발생했습니다.");
        return;
      }

      alert("게시글이 작성되었습니다.");
      // 예: 내 게시글 목록으로 이동
      router.push("/mypage");
    } catch (e) {
      console.error(e);
      setError("게시글 작성 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-slate-50">
      <div className="max-w-3xl mx-auto px-4 py-8">
        <h1 className="text-xl font-bold text-slate-900 mb-4">게시글 작성</h1>

        <form
          onSubmit={handleSubmit}
          className="bg-white rounded-2xl border border-slate-100 shadow-sm p-4 space-y-4"
        >
          <div>
            <label className="block text-sm font-medium text-slate-800 mb-1">
              제목
            </label>
            <input
              type="text"
              value={title}
              onChange={(e: ChangeEvent<HTMLInputElement>) =>
                setTitle(e.target.value)
              }
              className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm"
              placeholder="제목을 입력해 주세요."
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-800 mb-1">
              내용
            </label>
            <textarea
              value={content}
              onChange={(e) => setContent(e.target.value)}
              className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm min-h-[180px]"
              placeholder="여행 내용이나 기록을 자유롭게 작성해 보세요."
            />
          </div>

          <div className="flex items-center gap-2">
            <input
              id="isPublic"
              type="checkbox"
              checked={isPublic}
              onChange={(e) => setIsPublic(e.target.checked)}
              className="h-4 w-4"
            />
            <label htmlFor="isPublic" className="text-sm text-slate-700">
              전체 공개 (체크 해제 시 비공개)
            </label>
          </div>

          {error && <p className="text-sm text-red-500">{error}</p>}

          <div className="flex justify-end gap-2">
            <button
              type="button"
              onClick={() => router.back()}
              className="px-3 py-1.5 rounded-full border border-slate-200 text-sm text-slate-700 hover:bg-slate-50"
            >
              취소
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-1.5 rounded-full text-sm font-semibold bg-blue-500 text-white hover:bg-blue-600 disabled:opacity-50"
            >
              {loading ? "작성 중..." : "작성하기"}
            </button>
          </div>
        </form>
      </div>
    </main>
  );
}
