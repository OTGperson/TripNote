"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

// TS 타입 (백엔드 DestinationSummary 기준)
type Destination = {
  id: number;
  title: string;
  firstImage?: string | null;
  areaCode: string; // "1", "6", "31" ...
  contentTypeId: number; // 12, 14, 39 ...
};

const AREA_LABELS: Record<string, string> = {
  "1": "서울",
  "2": "인천",
  "3": "대전",
  "4": "대구",
  "5": "광주",
  "6": "부산",
  "7": "울산",
  "8": "세종",
  "31": "경기도",
  "32": "강원도",
  "33": "충청북도",
  "34": "충청남도",
  "35": "경상북도",
  "36": "경상남도",
  "37": "전라북도",
  "38": "전라남도",
  "39": "제주도",
};

const CONTENT_TYPE_LABELS: Record<number, string> = {
  12: "관광지",
  14: "문화시설",
  15: "축제·공연·행사",
  25: "여행코스",
  28: "레포츠",
  32: "숙박",
  38: "쇼핑",
  39: "음식점",
};

const PAGE_SIZE = 15; // 5개 x 3줄

type JwtPayload = {
  exp?: number;
  [key: string]: any;
};

function parseJwt(token: string): JwtPayload | null {
  try {
    const [, payload] = token.split(".");
    const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    const json = atob(base64);
    return JSON.parse(json);
  } catch {
    return null;
  }
}

export default function HomePage() {
  const [destinations, setDestinations] = useState<Destination[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [username, setUsername] = useState<string | null>(null);
  const [nickname, setNickname] = useState<string | null>(null);
  const [role, setRole] = useState<string | null>(null);

  const [selectedAreaCode, setSelectedAreaCode] = useState<string>("1"); // 기본: 서울
  const [selectedContentType, setSelectedContentType] = useState<
    number | "ALL"
  >("ALL");

  const [favoriteIds, setFavoriteIds] = useState<number[]>([]);

  const [currentPage, setCurrentPage] = useState(1);
  const router = useRouter();

  // 데이터 로딩
  useEffect(() => {
    const fetchDestinations = async () => {
      if (!API_BASE_URL) {
        setError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
        return;
      }
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(`${API_BASE_URL}/api/v1/dest`);
        if (!res.ok) {
          throw new Error("목록 조회 실패");
        }
        const data: Destination[] = await res.json();
        setDestinations(data);
      } catch (e) {
        console.error(e);
        setError("여행지 목록을 불러오는 데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchDestinations();
  }, []);

  useEffect(() => {
    if (typeof window === "undefined") return;

    const token = localStorage.getItem("accessToken");
    const storedNickname = localStorage.getItem("nickname");
    const storedUsername = localStorage.getItem("username");
    const storedRole = localStorage.getItem("role");

    setRole(storedRole);

    if (!token) {
      setIsLoggedIn(false);
      setNickname(null);
      return;
    }

    const payload = parseJwt(token);
    const nowSec = Math.floor(Date.now() / 1000);

    // exp 없거나 이미 만료된 토큰 → 강제 로그아웃
    if (!payload?.exp || payload.exp <= nowSec) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("nickname");
      localStorage.removeItem("username");
      localStorage.removeItem("role");
      setIsLoggedIn(false);
      setNickname(null);
      setRole(null);
      return;
    }

    // 아직 유효한 토큰이면 로그인 상태 유지
    setIsLoggedIn(true);
    setNickname(storedNickname ?? storedUsername ?? null);

    // 남은 시간 후 자동 로그아웃 타이머
    const timeoutMs = (payload.exp - nowSec) * 1000;

    const timerId = window.setTimeout(() => {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("nickname");
      localStorage.removeItem("username");
      localStorage.removeItem("role");
      setIsLoggedIn(false);
      setNickname(null);
      setRole(null);
      alert("로그인 시간이 만료되었습니다. 다시 로그인 해주세요.");
    }, timeoutMs);

    return () => {
      window.clearTimeout(timerId);
    };
  }, []);

  const handleLogout = () => {
    if (typeof window !== "undefined") {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("username");
      localStorage.removeItem("nickname");
      localStorage.removeItem("role");
    }
    setIsLoggedIn(false);
    setUsername(null);
    setNickname(null);
    setRole(null);
    router.push("/");
  };

  // 필터링된 목록
  const filtered = useMemo(() => {
    return destinations.filter((d) => {
      const matchArea = d.areaCode === selectedAreaCode;
      const matchType =
        selectedContentType === "ALL"
          ? true
          : d.contentTypeId === selectedContentType;
      return matchArea && matchType;
    });
  }, [destinations, selectedAreaCode, selectedContentType]);

  // 페이지네이션
  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const page = Math.min(currentPage, totalPages);
  const start = (page - 1) * PAGE_SIZE;
  const end = start + PAGE_SIZE;
  const pagedDestinations = filtered.slice(start, end);

  const handleChangeArea = (code: string) => {
    setSelectedAreaCode(code);
    setCurrentPage(1);
  };

  const handleChangeContentType = (type: number | "ALL") => {
    setSelectedContentType(type);
    setCurrentPage(1);
  };

  const areaButtons = Object.entries(AREA_LABELS);
  const contentTypeButtons: (number | "ALL")[] = [
    "ALL",
    ...Object.keys(CONTENT_TYPE_LABELS).map(Number),
  ];

  // 즐겨찾기
  const handleToggleFavorite = async (destId: number) => {
    if (typeof window === "undefined") return;

    const token = localStorage.getItem("accessToken");

    if (!token) {
      const goLogin = window.confirm(
        "즐겨찾기는 로그인 후 이용 가능합니다.\n로그인 하러 가시겠습니까?"
      );
      if (goLogin) {
        router.push("/login");
      }
      return;
    }

    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/favorites/${destId}/toggle`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const data = await res.json().catch(() => ({}));

      if (!res.ok || data.success === false) {
        alert(data.message ?? "즐겨찾기 처리 중 오류가 발생했습니다.");
        return;
      }

      const isNowFavorite: boolean = data.data ?? data;

      setFavoriteIds((prev) =>
        isNowFavorite ? [...prev, destId] : prev.filter((id) => id !== destId)
      );
    } catch (e) {
      console.error(e);
      alert("즐겨찾기 처리 중 오류가 발생했습니다.");
    }
  };

  useEffect(() => {
    if (typeof window === "undefined") return;
    if (!API_BASE_URL) return;

    const token = localStorage.getItem("accessToken");
    if (!token) return; // 비회원이면 패스

    (async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/api/v1/favorites/my`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const body = await res.json().catch(() => ({}));

        if (!res.ok || body.success === false) {
          return;
        }

        const ids: number[] = body.data ?? body;
        setFavoriteIds(ids);
      } catch (e) {
        console.error("초기 즐겨찾기 목록 조회 실패", e);
      }
    })();
  }, []);

  return (
    // 🔹 헤더가 fixed가 되면서 겹치지 않도록 pt-16(대략 64px) 추가
    <div className="min-h-screen bg-slate-50 flex flex-col pt-16">
      {/* 헤더 */}
      <header className="fixed top-0 left-0 right-0 z-50 w-full border-b bg-white/80 backdrop-blur-sm">
        <div className="max-w-6xl mx-auto flex items-center justify-between px-4 py-3">
          {/* 로고 */}
          <Link href="/" className="flex items-center gap-3">
            <img
              src="/logo.png"
              alt="TripNote 로고"
              className="h-10 w-10 rounded-full object-cover border border-slate-200"
            />
            <span className="text-xl font-bold text-slate-900">TripNote</span>
          </Link>

          {/* 오른쪽 로그인 / 회원가입 or 유저명 / 로그아웃 */}
          <nav className="flex items-center gap-3">
            {/* 🔹 관리자에게만 보이는 버튼 */}
            {role === "ADMIN" && (
              <button
                type="button"
                onClick={async () => {
                  const token = localStorage.getItem("accessToken");
                  if (!token) {
                    alert("로그인이 필요합니다.");
                    return;
                  }

                  const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;
                  if (!API_BASE_URL) {
                    alert("API 주소 설정이 필요합니다.");
                    return;
                  }

                  const res = await fetch(
                    `${API_BASE_URL}/api/v1/dest/admin/sync`,
                    {
                      method: "POST",
                      headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                      },
                    }
                  );

                  const data = await res.json().catch(() => ({}));
                  if (!res.ok || data.success === false) {
                    alert(data.message ?? "동기화 중 오류가 발생했습니다.");
                    return;
                  }

                  alert("전국 여행지 동기화가 완료되었습니다.");
                }}
                className="px-3 py-1.5 rounded-full text-xs font-semibold bg-amber-500 text-white hover:bg-amber-600"
              >
                여행지 동기화
              </button>
            )}
            {role === "ADMIN" && (
              <button
                type="button"
                onClick={async () => {
                  const token = localStorage.getItem("accessToken");
                  if (!token) {
                    alert("로그인이 필요합니다.");
                    return;
                  }

                  const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;
                  if (!API_BASE_URL) {
                    alert("API 주소 설정이 필요합니다.");
                    return;
                  }

                  const res = await fetch(
                    `${API_BASE_URL}/api/v1/dest/admin/sync/details`,
                    {
                      method: "POST",
                      headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                      },
                    }
                  );

                  const data = await res.json().catch(() => ({}));
                  if (!res.ok || data.success === false) {
                    alert(data.message ?? "동기화 중 오류가 발생했습니다.");
                    return;
                  }

                  alert("여행지의 상세설명 추가가 완료되었습니다.");
                }}
                className="px-3 py-1.5 rounded-full text-xs font-semibold bg-amber-500 text-white hover:bg-amber-600"
              >
                여행지 상세설명 추가
              </button>
            )}

            {/* 일반 로그인/회원가입 버튼들 */}
            {isLoggedIn ? (
              <>
                {/* 나중에 “마이페이지” 같은 것도 여기 추가 가능 */}
                {nickname && (
                  <span className="text-sm text-slate-700 mr-2">
                    {nickname}님, 안녕하세요 👋
                  </span>
                )}
                <button
                  type="button"
                  onClick={handleLogout}
                  className="px-3 py-1.5 rounded-full text-sm font-medium text-slate-700 hover:bg-slate-100"
                >
                  로그아웃
                </button>
              </>
            ) : (
              <>
                <Link
                  href="/login"
                  className="px-3 py-1.5 rounded-full text-sm font-medium text-slate-700 hover:bg-slate-100"
                >
                  로그인
                </Link>
                <Link
                  href="/signup"
                  className="px-4 py-1.5 rounded-full text-sm font-semibold bg-blue-500 text-white hover:bg-blue-600 shadow-sm"
                >
                  회원가입
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>

      {/* 메인 컨텐츠 */}
      <main className="flex-1">
        <div className="max-w-6xl mx-auto px-4 py-6 flex gap-4">
          {/* 왼쪽: 여행지 카드 그리드 */}
          <section className="flex-1 min-w-0">
            <div className="flex items-center justify-between mb-3">
              <h1 className="text-lg font-semibold text-slate-900">
                여행지 목록
              </h1>
              <p className="text-xs text-slate-500">
                총 {filtered.length}개 / {page} 페이지
              </p>
            </div>

            {loading && (
              <p className="text-sm text-slate-500">불러오는 중입니다...</p>
            )}
            {error && <p className="text-sm text-red-500">{error}</p>}

            {!loading && !error && filtered.length === 0 && (
              <p className="text-sm text-slate-500">
                선택한 조건에 해당하는 여행지가 없습니다.
              </p>
            )}

            {/* 카드 그리드 */}
            <div
              className="grid gap-4"
              style={{
                gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
              }}
            >
              {pagedDestinations.map((dest) => {
                const areaLabel = AREA_LABELS[dest.areaCode] ?? "알 수 없음";
                const typeLabel =
                  CONTENT_TYPE_LABELS[dest.contentTypeId] ?? "기타";

                const hasImage =
                  !!dest.firstImage && dest.firstImage.trim() !== "";

                return (
                  <article
                    key={dest.id}
                    className="bg-white rounded-xl shadow-sm overflow-hidden flex flex-col border border-slate-100"
                  >
                    {/* 이미지 영역 - 3:4 비율 */}
                    <div
                      className="w-full overflow-hidden"
                      style={{
                        position: "relative",
                        paddingBottom: "133.33%", // 4 / 3 * 100 (3:4 비율)
                      }}
                    >
                      {hasImage ? (
                        <img
                          src={dest.firstImage as string}
                          alt={dest.title}
                          className="absolute inset-0 w-full h-full object-cover"
                        />
                      ) : (
                        <div className="absolute inset-0 flex items-center justify-center bg-slate-100 px-3 text-center text-xs text-slate-500">
                          이미지 정보가 없습니다.
                        </div>
                      )}
                    </div>

                    {/* 본문 */}
                    <div className="p-3 flex flex-col gap-1 flex-1">
                      <div className="flex items-start justify-between gap-2">
                        <h2 className="text-sm font-semibold text-slate-900 line-clamp-2">
                          {dest.title}
                        </h2>
                        <button
                          type="button"
                          onClick={() => handleToggleFavorite(dest.id)}
                          className={`inline-flex items-center justify-center w-8 h-8 rounded-full border transition
    ${
      favoriteIds.includes(dest.id)
        ? "border-yellow-400 bg-yellow-100"
        : "border-slate-300 bg-white hover:bg-slate-50"
    }
  `}
                          aria-label={
                            favoriteIds.includes(dest.id)
                              ? "즐겨찾기 해제"
                              : "즐겨찾기 추가"
                          }
                        >
                          <span
                            className={
                              favoriteIds.includes(dest.id)
                                ? "text-yellow-400"
                                : "text-slate-400"
                            }
                          >
                            {favoriteIds.includes(dest.id) ? "★" : "☆"}
                          </span>
                        </button>
                      </div>

                      <p className="text-xs text-slate-500">
                        {areaLabel} · {typeLabel}
                      </p>

                      <div className="mt-auto pt-2">
                        <button
                          type="button"
                          className="w-full inline-flex items-center justify-center rounded-full border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50 cursor-pointer"
                          onClick={() => router.push(`/dest/${dest.id}`)}
                        >
                          자세히 보기
                        </button>
                      </div>
                    </div>
                  </article>
                );
              })}
            </div>

            {/* 페이지네이션 */}
            {filtered.length > 0 && (
              <div className="mt-4 flex items-center justify-center gap-4">
                <button
                  type="button"
                  className="px-3 py-1.5 rounded-full text-sm border border-slate-200 text-slate-700 disabled:opacity-40"
                  onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                  disabled={page <= 1}
                >
                  ◀ 이전
                </button>
                <span className="text-xs text-slate-500">
                  {page} / {totalPages}
                </span>
                <button
                  type="button"
                  className="px-3 py-1.5 rounded-full text-sm border border-slate-200 text-slate-700 disabled:opacity-40"
                  onClick={() =>
                    setCurrentPage((p) => Math.min(totalPages, p + 1))
                  }
                  disabled={page >= totalPages}
                >
                  다음 ▶
                </button>
              </div>
            )}
          </section>

          {/* 오른쪽: 필터 영역 */}
          <aside className="shrink-0 space-y-4 sticky top-24 self-start w-28 sm:w-32 md:w-40 lg:w-52 min-w-[130px]">
            {/* 지역 선택 */}
            <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-3">
              <h2 className="text-sm font-semibold text-slate-900 mb-2">
                지역 선택
              </h2>
              <div className="flex flex-wrap gap-1">
                {areaButtons.map(([code, label]) => (
                  <button
                    key={code}
                    type="button"
                    onClick={() => handleChangeArea(code)}
                    className={`px-2.5 py-1 rounded-full text-xs border ${
                      selectedAreaCode === code
                        ? "bg-blue-500 text-white border-blue-500"
                        : "border-slate-200 text-slate-700 hover:bg-slate-50"
                    }`}
                  >
                    {label}
                  </button>
                ))}
              </div>
            </div>

            {/* 여행 종류 선택 */}
            <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-3">
              <h2 className="text-sm font-semibold text-slate-900 mb-2">
                여행 종류
              </h2>
              <div className="flex flex-wrap gap-1">
                {contentTypeButtons.map((type) => {
                  const isAll = type === "ALL";
                  const label = isAll
                    ? "전체"
                    : CONTENT_TYPE_LABELS[type as number] ?? "기타";
                  const active = selectedContentType === type;
                  return (
                    <button
                      key={String(type)}
                      type="button"
                      onClick={() => handleChangeContentType(type)}
                      className={`px-2.5 py-1 rounded-full text-xs border ${
                        active
                          ? "bg-emerald-500 text-white border-emerald-500"
                          : "border-slate-200 text-slate-700 hover:bg-slate-50"
                      }`}
                    >
                      {label}
                    </button>
                  );
                })}
              </div>
            </div>
          </aside>
        </div>
      </main>
    </div>
  );
}
