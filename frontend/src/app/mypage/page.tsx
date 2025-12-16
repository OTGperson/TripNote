"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

type Destination = {
  id: number | string;
  title: string;
  firstImage?: string | null;
  areaCode: string;
  contentTypeId: number;
};

type Post = {
  id: number;
  title: string;
  content: string;
  isPublic?: boolean;
  public?: boolean;
  createdAt?: string;
  updatedAt?: string;
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

const FAV_PAGE_SIZE = 6;

// 즐겨찾기 응답을 숫자 ID 배열로 바꾸는 유틸
function extractFavoriteIds(raw: any): number[] {
  if (!Array.isArray(raw)) return [];

  return raw
    .map((item) => {
      if (typeof item === "number") return item;
      if (typeof item === "string") return Number(item);

      if (item && typeof item === "object") {
        if (typeof item.destinationId === "number")
          return item.destinationId as number;
        if (typeof item.destinationId === "string")
          return Number(item.destinationId);
        if (typeof item.id === "number") return item.id as number;
        if (typeof item.id === "string") return Number(item.id);
      }
      return NaN;
    })
    .filter((n) => typeof n === "number" && !Number.isNaN(n));
}

export default function MyPage() {
  const router = useRouter();

  // 유저 정보
  const [nickname, setNickname] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);
  const [role, setRole] = useState<string | null>(null);

  // 🔹 탭 상태: 즐겨찾기 / 작성한 게시글
  const [activeTab, setActiveTab] = useState<"favorites" | "posts">(
    "favorites"
  );

  // 즐겨찾기 여행지들
  const [favorites, setFavorites] = useState<Destination[]>([]);
  const [favoriteIds, setFavoriteIds] = useState<number[]>([]);

  const [loadingFavorites, setLoadingFavorites] = useState(false);
  const [favoritesError, setFavoritesError] = useState<string | null>(null);

  // 즐겨찾기 필터: 기본은 전체 지역 / 전체 타입
  const [selectedAreaCode, setSelectedAreaCode] = useState<string | "ALL">(
    "ALL"
  );
  const [selectedContentType, setSelectedContentType] = useState<
    number | "ALL"
  >("ALL");

  // 🔹 즐겨찾기 탭용 페이지네이션
  const [favPage, setFavPage] = useState(1);

  // 내가 작성한 게시글
  const [posts, setPosts] = useState<Post[]>([]);
  const [loadingPosts, setLoadingPosts] = useState(false);
  const [postsError, setPostsError] = useState<string | null>(null);

  // 1) 로그인 여부 & 유저 정보 세팅 + 비로그인 시 로그인 페이지로
  useEffect(() => {
    if (typeof window === "undefined") return;

    const token = localStorage.getItem("accessToken");
    const storedNickname = localStorage.getItem("nickname");
    const storedUsername = localStorage.getItem("username");
    const storedRole = localStorage.getItem("role");

    if (!token) {
      alert("로그인 후 이용 가능한 페이지입니다.");
      router.replace("/login");
      return;
    }

    setNickname(storedNickname ?? storedUsername ?? null);
    setUsername(storedUsername ?? null);
    setRole(storedRole ?? null);
  }, [router]);

  // 2) 내 즐겨찾기 ID 목록 가져오기 + 각 여행지 정보 로딩
  useEffect(() => {
    if (!API_BASE_URL) {
      setFavoritesError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }
    if (typeof window === "undefined") return;

    const token = localStorage.getItem("accessToken");
    if (!token) return;

    const fetchMyFavorites = async () => {
      setLoadingFavorites(true);
      setFavoritesError(null);

      try {
        // 2-1) 먼저 /favorites/my 에서 ID 목록 가져오기
        const res = await fetch(`${API_BASE_URL}/api/v1/favorites/my`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const body = await res.json().catch(() => ({}));

        if (!res.ok || body.success === false) {
          setFavoritesError(
            body.message ?? "즐겨찾기 목록을 불러오는 중 오류가 발생했습니다."
          );
          setFavorites([]);
          setFavoriteIds([]);
          return;
        }

        const raw = body.data ?? body;
        const ids = extractFavoriteIds(raw);

        setFavoriteIds(ids);

        if (ids.length === 0) {
          setFavorites([]);
          return;
        }

        // 2-2) 각 ID에 대해 /dest/{id} 상세정보 병렬 요청
        const detailPromises = ids.map(async (id) => {
          try {
            const r = await fetch(`${API_BASE_URL}/api/v1/dest/${id}`);
            if (!r.ok) return null;
            const d: Destination = await r.json();
            return d;
          } catch {
            return null;
          }
        });

        const results = await Promise.all(detailPromises);
        const valid = results.filter(
          (d): d is Destination => d !== null && d !== undefined
        );

        setFavorites(valid);
      } catch (e) {
        console.error(e);
        setFavoritesError("즐겨찾기 여행지를 불러오는 중 오류가 발생했습니다.");
      } finally {
        setLoadingFavorites(false);
      }
    };

    fetchMyFavorites();
  }, []);

  // 3) 즐겨찾기 필터 적용
  const filteredFavorites = useMemo(() => {
    return favorites.filter((d) => {
      const matchArea =
        selectedAreaCode === "ALL" ? true : d.areaCode === selectedAreaCode;
      const matchType =
        selectedContentType === "ALL"
          ? true
          : d.contentTypeId === selectedContentType;
      return matchArea && matchType;
    });
  }, [favorites, selectedAreaCode, selectedContentType]);

  // 🔹 즐겨찾기 페이지네이션 계산
  const favTotalPages = Math.max(
    1,
    Math.ceil(filteredFavorites.length / FAV_PAGE_SIZE)
  );
  const safeFavPage = Math.min(favPage, favTotalPages);
  const favStart = (safeFavPage - 1) * FAV_PAGE_SIZE;
  const favEnd = favStart + FAV_PAGE_SIZE;
  const pagedFavorites = filteredFavorites.slice(favStart, favEnd);

  const areaButtons = [
    ["ALL", "전체"],
    ...Object.entries(AREA_LABELS), // ["1","서울"], ...
  ];

  const contentTypeButtons: (number | "ALL")[] = [
    "ALL",
    ...Object.keys(CONTENT_TYPE_LABELS).map(Number),
  ];

  const handleChangeArea = (code: string | "ALL") => {
    setSelectedAreaCode(code);
    setFavPage(1);
  };

  const handleChangeContentType = (type: number | "ALL") => {
    setSelectedContentType(type);
    setFavPage(1);
  };

  // 4) 즐겨찾기 토글 (마이페이지에서는 해제 시 목록에서 제거)
  const handleToggleFavorite = async (dest: Destination) => {
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

    if (!API_BASE_URL) {
      alert("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }

    const numericId = Number(dest.id);
    if (Number.isNaN(numericId)) {
      console.error("잘못된 destination ID:", dest.id);
      return;
    }

    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/favorites/${numericId}/toggle`,
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

      if (!isNowFavorite) {
        // 즐겨찾기 해제된 경우 → 마이페이지 목록에서 제거
        setFavoriteIds((prev) => prev.filter((id) => id !== numericId));
        setFavorites((prev) => prev.filter((d) => Number(d.id) !== numericId));
      } else {
        // 다시 추가된 경우(안 쓰일 가능성이 크지만 안전하게 처리)
        setFavoriteIds((prev) =>
          prev.includes(numericId) ? prev : [...prev, numericId]
        );
      }
    } catch (e) {
      console.error(e);
      alert("즐겨찾기 처리 중 오류가 발생했습니다.");
    }
  };

  const handleGoDetail = (id: number | string) => {
    router.push(`/dest/${id}`);
  };

  // 🔹 내가 작성한 게시글 로딩 (탭이 게시글일 때 최초 한 번)
  useEffect(() => {
    if (activeTab !== "posts") return;
    if (!API_BASE_URL) {
      setPostsError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }
    if (typeof window === "undefined") return;

    const token = localStorage.getItem("accessToken");
    if (!token) return;

    const fetchMyPosts = async () => {
      setLoadingPosts(true);
      setPostsError(null);
      try {
        const res = await fetch(`${API_BASE_URL}/api/v1/post/me`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const body = await res.json().catch(() => ({}));

        if (!res.ok || body.success === false) {
          setPostsError(
            body.message ??
              "내가 작성한 게시글 목록을 불러오는 중 오류가 발생했습니다."
          );
          setPosts([]);
          return;
        }

        const raw = body.data ?? body;
        if (Array.isArray(raw)) {
          setPosts(raw as Post[]);
        } else {
          setPosts([]);
        }
      } catch (e) {
        console.error(e);
        setPostsError(
          "내가 작성한 게시글 목록을 불러오는 중 오류가 발생했습니다."
        );
      } finally {
        setLoadingPosts(false);
      }
    };

    fetchMyPosts();
  }, [activeTab]);

  const formatDate = (value?: string) => {
    if (!value) return "";
    // "2025-12-10T12:34:56" → "2025-12-10"
    return value.slice(0, 10);
  };

  return (
    <main className="min-h-screen bg-slate-50">
      <div className="max-w-6xl mx-auto px-4 py-8 space-y-6">
        {/* 상단: 유저 정보 카드 */}
        <section className="bg-white rounded-2xl shadow-sm border border-slate-100 p-5 md:p-6 flex flex-col gap-3">
          <div className="flex items-center justify-between gap-3">
            <div className="flex-1">
              <p className="text-sm text-slate-500">마이페이지</p>
              <h1 className="text-xl font-bold text-slate-900">
                {nickname ? `${nickname}님, 안녕하세요 👋` : "안녕하세요 👋"}
              </h1>
              {username && (
                <p className="text-xs text-slate-500 mt-1">
                  아이디: <span className="font-medium">{username}</span>
                  {role && (
                    <>
                      {" · "}
                      <span className="inline-flex items-center px-2 py-0.5 rounded-full bg-slate-100 text-[11px] font-semibold text-slate-700">
                        {role === "ADMIN" ? "관리자" : "일반 사용자"}
                      </span>
                    </>
                  )}
                </p>
              )}
            </div>

            {/* 메인화면으로 이동 버튼 */}
            <button
              type="button"
              onClick={() => router.push("/")}
              className="px-3 py-1.5 rounded-full text-xs border border-slate-200 text-slate-700 hover:bg-slate-50"
            >
              메인화면으로
            </button>
          </div>

          <div className="flex flex-wrap items-center gap-2 text-xs text-slate-500 mt-1">
            <span className="inline-flex items-center px-2 py-1 rounded-full bg-slate-100">
              즐겨찾기한 여행지{" "}
              <span className="font-semibold text-slate-800 ml-1">
                {favorites.length}개
              </span>
            </span>
          </div>

          {/* 🔹 탭 + 게시글 작성 버튼 */}
          <div className="mt-4 flex items-center justify-between border-b border-slate-200 pb-2">
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => setActiveTab("favorites")}
                className={`px-3 py-1.5 text-sm rounded-full ${
                  activeTab === "favorites"
                    ? "bg-slate-900 text-white"
                    : "text-slate-600 hover:bg-slate-100"
                }`}
              >
                즐겨찾기한 여행지
              </button>
              <button
                type="button"
                onClick={() => setActiveTab("posts")}
                className={`px-3 py-1.5 text-sm rounded-full ${
                  activeTab === "posts"
                    ? "bg-slate-900 text-white"
                    : "text-slate-600 hover:bg-slate-100"
                }`}
              >
                내가 작성한 게시글
              </button>
            </div>

            <button
              type="button"
              onClick={() => router.push("/posts/new")}
              className="px-3 py-1.5 rounded-full text-sm font-medium bg-blue-500 text-white hover:bg-blue-600 shadow-sm"
            >
              게시글 작성
            </button>
          </div>
        </section>

        {/* 🔹 탭별 내용 렌더링 */}
        {activeTab === "favorites" ? (
          // =========================
          // 즐겨찾기한 여행지 탭
          // =========================
          <section className="flex flex-col md:flex-row gap-4">
            {/* 좌측: 필터 */}
            <aside className="w-full md:w-64 shrink-0 space-y-4">
              <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-3">
                <h2 className="text-sm font-semibold text-slate-900 mb-2">
                  지역 필터
                </h2>
                <div className="flex flex-wrap gap-1">
                  {areaButtons.map(([code, label]) => (
                    <button
                      key={code}
                      type="button"
                      onClick={() => handleChangeArea(code as string | "ALL")}
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

            {/* 우측: 즐겨찾기 카드 리스트 */}
            <section className="flex-1 min-w-0">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-lg font-semibold text-slate-900">
                  즐겨찾기한 여행지
                </h2>
                <p className="text-xs text-slate-500">
                  총 {filteredFavorites.length}개
                  {selectedAreaCode !== "ALL" && (
                    <>
                      {" "}
                      · 지역:{" "}
                      {AREA_LABELS[selectedAreaCode as string] ?? "전체"}
                    </>
                  )}
                </p>
              </div>

              {loadingFavorites && (
                <p className="text-sm text-slate-500">
                  즐겨찾기 목록을 불러오는 중입니다...
                </p>
              )}
              {favoritesError && (
                <p className="text-sm text-red-500">{favoritesError}</p>
              )}

              {!loadingFavorites &&
                !favoritesError &&
                filteredFavorites.length === 0 && (
                  <p className="text-sm text-slate-500">
                    조건에 해당하는 즐겨찾기 여행지가 없습니다.
                  </p>
                )}

              <div
                className="grid gap-4 mt-2"
                style={{
                  gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
                }}
              >
                {pagedFavorites.map((dest) => {
                  const areaLabel = AREA_LABELS[dest.areaCode] ?? "알 수 없음";
                  const typeLabel =
                    CONTENT_TYPE_LABELS[dest.contentTypeId] ?? "기타";

                  const hasImage =
                    !!dest.firstImage && dest.firstImage.trim() !== "";

                  const numericId = Number(dest.id);
                  const isFav =
                    !Number.isNaN(numericId) && favoriteIds.includes(numericId);

                  return (
                    <article
                      key={dest.id}
                      className="bg-white rounded-xl shadow-sm overflow-hidden flex flex-col border border-slate-100 max-w-xs w-full mx-auto"
                    >
                      {/* 이미지 */}
                      <div
                        className="w-full overflow-hidden"
                        style={{
                          position: "relative",
                          paddingBottom: "133.33%", // 3:4 비율
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

                      {/* 내용 */}
                      <div className="p-3 flex flex-col gap-1 flex-1">
                        <div className="flex items-start justify-between gap-2">
                          <h3 className="text-sm font-semibold text-slate-900 line-clamp-2">
                            {dest.title}
                          </h3>
                          <button
                            type="button"
                            onClick={() => handleToggleFavorite(dest)}
                            className={`inline-flex items-center justify-center w-8 h-8 rounded-full border transition
    ${
      isFav
        ? "border-yellow-400 bg-yellow-100"
        : "border-slate-300 bg-white hover:bg-slate-50"
    }
  `}
                            aria-label={
                              isFav ? "즐겨찾기 해제" : "즐겨찾기 추가"
                            }
                          >
                            <span
                              className={
                                isFav ? "text-yellow-400" : "text-slate-400"
                              }
                            >
                              {isFav ? "★" : "☆"}
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
                            onClick={() => handleGoDetail(dest.id)}
                          >
                            자세히 보기
                          </button>
                        </div>
                      </div>
                    </article>
                  );
                })}
              </div>

              {/* 🔹 즐겨찾기 페이징 (6개 초과 시) */}
              {filteredFavorites.length > FAV_PAGE_SIZE && (
                <div className="mt-4 flex items-center justify-center gap-4">
                  <button
                    type="button"
                    className="px-3 py-1.5 rounded-full text-sm border border-slate-200 text-slate-700 disabled:opacity-40"
                    onClick={() => setFavPage((p) => Math.max(1, p - 1))}
                    disabled={safeFavPage <= 1}
                  >
                    ◀ 이전
                  </button>
                  <span className="text-xs text-slate-500">
                    {safeFavPage} / {favTotalPages}
                  </span>
                  <button
                    type="button"
                    className="px-3 py-1.5 rounded-full text-sm border border-slate-200 text-slate-700 disabled:opacity-40"
                    onClick={() =>
                      setFavPage((p) => Math.min(favTotalPages, p + 1))
                    }
                    disabled={safeFavPage >= favTotalPages}
                  >
                    다음 ▶
                  </button>
                </div>
              )}
            </section>
          </section>
        ) : (
          // =========================
          // 내가 작성한 게시글 탭
          // =========================
          <section className="bg-white rounded-2xl shadow-sm border border-slate-100 p-5 md:p-6">
            <div className="flex items-center justify-between mb-3">
              <h2 className="text-lg font-semibold text-slate-900">
                내가 작성한 게시글
              </h2>
            </div>

            {loadingPosts && (
              <p className="text-sm text-slate-500">
                게시글을 불러오는 중입니다...
              </p>
            )}

            {postsError && <p className="text-sm text-red-500">{postsError}</p>}

            {!loadingPosts && !postsError && posts.length === 0 && (
              <p className="text-sm text-slate-500">
                작성한 게시글이 없습니다. 상단의 &quot;게시글 작성&quot;
                버튼으로 첫 글을 작성해 보세요.
              </p>
            )}

            <div className="mt-3 space-y-3">
              {posts.map((post) => {
                const isPublic = post.isPublic ?? post.public ?? false;
                const created = formatDate(post.createdAt);
                const updated = formatDate(post.updatedAt);

                return (
                  <article
                    key={post.id}
                    className="border border-slate-100 rounded-xl p-4 flex flex-col gap-1"
                  >
                    <div className="flex items-start justify-between gap-3">
                      <h3 className="text-sm md:text-base font-semibold text-slate-900 line-clamp-2">
                        {post.title}
                      </h3>
                      <span
                        className={`px-2 py-0.5 rounded-full text-[11px] font-semibold ${
                          isPublic
                            ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                            : "bg-slate-100 text-slate-700 border border-slate-200"
                        }`}
                      >
                        {isPublic ? "전체 공개" : "비공개"}
                      </span>
                    </div>

                    <p className="text-xs text-slate-400">
                      {created && `작성일: ${created}`}
                      {updated &&
                        updated !== created &&
                        ` · 수정일: ${updated}`}
                    </p>

                    <p className="mt-1 text-sm text-slate-700 line-clamp-2">
                      {post.content}
                    </p>
                  </article>
                );
              })}
            </div>
          </section>
        )}
      </div>
    </main>
  );
}
