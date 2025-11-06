"use client";

import { useEffect, useState } from "react";

export default function ClientPage() {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

    fetch(`${API_BASE_URL}/api/v1/posts`)
      .then((response) => response.json())
      .then((data) => setPosts(data));
  }, []);

  return (
    // <>
    //   <h1>게시글 목록</h1>
    //    <ul>
    //      {/* eslint-disable-next-line @typescript-eslint/no-explicit-any */}
    //      {posts.map((post: any) => (
    //        <li key={post.id}>{post.title}</li>
    //      ))}
    //    </ul>
    // </>
    <></>
  );
}
