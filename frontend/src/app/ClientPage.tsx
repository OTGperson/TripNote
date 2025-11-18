"use client";

import Link from "next/link";

export default function ClientPage() {
  return (
    <main className="min-h-screen flex items-center justify-center px-4 bg-gradient-to-b from-sky-100 via-slate-50 to-indigo-100">
      <section className="w-full max-w-md bg-white/90 rounded-2xl shadow-2xl px-6 py-8 space-y-6">
        <header className="space-y-2">
          <h1 className="text-2xl font-bold text-slate-900">TripNote</h1>
          <p className="text-sm text-slate-500">
            여행을 기록하고, 추억을 정리해 보세요.
          </p>
        </header>

        <div className="space-y-4">
          <p className="text-sm text-slate-600 leading-relaxed">
            TripNote에 가입하고 나만의 여행 노트를 만들어 보세요.
            <br />
            이미 계정이 있다면 로그인을 통해 바로 시작할 수 있어요.
          </p>

          <div className="flex gap-3">
            <Link href="/signup" className="flex-1">
              <button className="w-full rounded-full bg-gradient-to-r from-blue-500 to-indigo-500 text-white font-semibold text-sm py-2.5 shadow-md hover:shadow-lg hover:translate-y-[-1px] active:translate-y-0 transition">
                회원가입
              </button>
            </Link>

            <Link href="/login" className="flex-1">
              <button className="w-full rounded-full border border-slate-300 bg-white text-slate-700 font-semibold text-sm py-2.5 hover:bg-slate-50 hover:border-slate-400 transition">
                로그인
              </button>
            </Link>
          </div>
        </div>

        <footer className="border-t border-slate-100 pt-4 text-xs text-slate-400 flex justify-between">
          <span>© {new Date().getFullYear()} TripNote</span>
          <span>v0.1.0</span>
        </footer>
      </section>
    </main>
  );
}
