"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

type DestinationDetail = {
  id: number;
  title: string;
  firstImage?: string | null;
  areaCode: string;
  sigunguCode?: string | null;
  addr1?: string | null;
  addr2?: string | null;
  contentTypeId: number;
  detail?: string | null;
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

export default function DestinationDetailPage() {
  const router = useRouter();
  const params = useParams();
  const id = params?.id as string;

  const [data, setData] = useState<DestinationDetail | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 🔹 이 페이지용 즐겨찾기 상태
  const [favorite, setFavorite] = useState(false);

  // 1) 상세 데이터 가져오기
  useEffect(() => {
    if (!id) return;
    if (!API_BASE_URL) {
      setError("NEXT_PUBLIC_API_BASE_URL가 설정되어 있지 않습니다.");
      return;
    }

    const fetchDetail = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(`${API_BASE_URL}/api/v1/dest/${id}`);
        if (!res.ok) {
          throw new Error(`detail fetch failed: ${res.status}`);
        }
        const json: DestinationDetail = await res.json();
        setData(json);
      } catch (e) {
        console.error(e);
        setError("여행지 정보를 불러오는 중 오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchDetail();
  }, [id]);

  // 2) 데이터 로딩 후, 이미 즐겨찾기인지 서버에 물어보기
  useEffect(() => {
    if (!data) return;
    if (typeof window === "undefined") return;
    if (!API_BASE_URL) return;

    const token = localStorage.getItem("accessToken");
    if (!token) return; // 비회원이면 그냥 패스

    (async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/api/v1/favorites/${data.id}`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const body = await res.json().catch(() => ({}));

        if (!res.ok || body.success === false) {
          return;
        }

        const isFav: boolean = body.data ?? body;
        setFavorite(isFav);
      } catch (e) {
        console.error("초기 즐겨찾기 상태 조회 실패", e);
      }
    })();
  }, [data]);

  const handleBack = () => {
    router.back();
  };

  // ⬇⬇⬇ 여기부터는 Hook 호출이 없음 (조건부 렌더링은 이제 안전함)

  if (loading) {
    return (
      <main className="min-h-screen flex items-center justify-center bg-slate-50">
        <p className="text-sm text-slate-500">
          여행지 정보를 불러오는 중입니다...
        </p>
      </main>
    );
  }

  if (error) {
    return (
      <main className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="bg-white rounded-xl shadow-sm border border-slate-100 px-6 py-4">
          <p className="text-sm text-red-500 mb-2">{error}</p>
          <button
            type="button"
            onClick={handleBack}
            className="px-4 py-1.5 rounded-full text-sm border border-slate-200 text-slate-700 hover:bg-slate-50"
          >
            돌아가기
          </button>
        </div>
      </main>
    );
  }

  if (!data) {
    return (
      <main className="min-h-screen flex items-center justify-center bg-slate-50">
        <p className="text-sm text-slate-500">표시할 여행지 정보가 없습니다.</p>
      </main>
    );
  }

  // 즐겨찾기 토글
  const handleToggleFavorite = async () => {
    if (!data) return;
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
        `${API_BASE_URL}/api/v1/favorites/${data.id}/toggle`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const body = await res.json().catch(() => ({}));

      if (!res.ok || body.success === false) {
        alert(body.message ?? "즐겨찾기 처리 중 오류가 발생했습니다.");
        return;
      }

      const isNowFavorite: boolean = body.data ?? body;
      setFavorite(isNowFavorite);
    } catch (e) {
      console.error(e);
      alert("즐겨찾기 처리 중 오류가 발생했습니다.");
    }
  };

  const areaLabel = AREA_LABELS[data.areaCode] ?? "알 수 없는 지역";
  const typeLabel = CONTENT_TYPE_LABELS[data.contentTypeId] ?? "기타";
  const hasImage = !!data.firstImage && data.firstImage.trim() !== "";
  const address =
    [data.addr1, data.addr2].filter((s) => s && s.trim() !== "").join(" ") ||
    "주소 정보 없음";
  const hasDetail =
    data.detail && data.detail.trim() !== "" && data.detail.trim() !== "-";

  return (
    <main className="min-h-screen bg-slate-50">
      <div className="max-w-5xl mx-auto px-4 pb-10">
        {/* 상단: 뒤로가기 + 브레드크럼 느낌 */}
        <div className="flex items-center justify-between py-4">
          <button
            type="button"
            onClick={handleBack}
            className="inline-flex items-center gap-1 text-sm text-slate-600 hover:text-slate-900"
          >
            <span>←</span>
            <span className="cursor-pointer">목록으로 돌아가기</span>
          </button>
        </div>

        {/* 메인 카드 */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
          {/* 상단 이미지 영역 */}
          <div
            className="w-full relative"
            style={{ paddingBottom: "50%" }} // 2:1 비율
          >
            {hasImage ? (
              <img
                src={data.firstImage as string}
                alt={data.title}
                className="absolute inset-0 w-full h-full object-cover"
              />
            ) : (
              <div className="absolute inset-0 flex flex-col items-center justify-center bg-slate-100 px-4 text-center">
                <p className="text-sm font-medium text-slate-600">
                  이미지 정보가 없습니다.
                </p>
                <p className="text-xs text-slate-400 mt-1">
                  대신 아래 상세 설명을 참고해 주세요.
                </p>
              </div>
            )}

            {/* 이미지 위 라벨들 */}
            <div className="absolute left-4 bottom-4 flex flex-wrap gap-2">
              <span className="px-2.5 py-1 rounded-full text-xs font-medium bg-black/60 text-white">
                {areaLabel}
              </span>
              <span className="px-2.5 py-1 rounded-full text-xs font-medium bg-white/85 text-slate-800">
                {typeLabel}
              </span>
            </div>
          </div>

          {/* 본문 내용 */}
          <div className="p-5 md:p-6 space-y-4">
            {/* 제목 + 태그 라인 */}
            <div>
              <div className="flex items-center gap-3">
                <h1 className="text-2xl font-bold text-slate-900">
                  {data.title}
                </h1>
                <button
                  type="button"
                  onClick={handleToggleFavorite}
                  className={`inline-flex items-center justify-center w-9 h-9 rounded-full border transition
    ${
      favorite
        ? "border-yellow-400 bg-yellow-100"
        : "border-slate-300 bg-white hover:bg-slate-50"
    }
  `}
                  aria-label={favorite ? "즐겨찾기 해제" : "즐겨찾기 추가"}
                >
                  <span
                    className={
                      favorite
                        ? "text-yellow-400 text-xl"
                        : "text-slate-400 text-xl"
                    }
                  >
                    {favorite ? "★" : "☆"}
                  </span>
                </button>
              </div>
              <p className="mt-1 text-xs text-slate-500">
                {areaLabel} · {typeLabel}
              </p>
            </div>

            {/* 주소 정보 박스 */}
            <div className="bg-slate-50 border border-slate-100 rounded-xl px-4 py-3 text-sm text-slate-700">
              <div className="flex items-start gap-2">
                <span className="mt-0.5 text-xs text-slate-400">📍</span>
                <div>
                  <p className="font-medium text-slate-800">주소</p>
                  <p className="mt-0.5 text-sm text-slate-700">{address}</p>
                </div>
              </div>
            </div>

            {/* 상세 설명 */}
            <section className="mt-2">
              <h2 className="text-sm font-semibold text-slate-900 mb-1.5">
                상세 설명
              </h2>
              {hasDetail ? (
                <p className="text-sm leading-relaxed text-slate-700 whitespace-pre-line">
                  {data.detail}
                </p>
              ) : (
                <p className="text-sm text-slate-500">
                  아직 등록된 설명이 없습니다.
                </p>
              )}
            </section>

            {/* 하단 버튼들 */}
            <div className="pt-3 flex flex-wrap gap-2 border-t border-slate-100 mt-3">
              <button
                type="button"
                onClick={handleBack}
                className="px-3 py-1.5 rounded-full border border-slate-200 text-xs text-slate-700 hover:bg-slate-50 cursor-pointer"
              >
                목록으로 돌아가기
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
