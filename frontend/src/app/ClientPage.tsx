"use client";

import { useEffect, useState } from "react";

export default function ClientPage() {
  const [members, setMembers] = useState([]);

  useEffect(() => {
    const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

    fetch(`${API_BASE_URL}/api/v1/member`)
      .then((response) => response.json())
      .then((data) => setMembers(data));
  }, []);

  return (
    <>
      <h1>멤버 목록2</h1>
      <ul>
        {/* eslint-disable-next-line @typescript-eslint/no-explicit-any */}
        {members.map((member: any) => (
          <li key={member.id}>{member.username}</li>
        ))}
      </ul>
    </>
  );
}
