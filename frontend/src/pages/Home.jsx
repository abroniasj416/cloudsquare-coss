import { useEffect, useState } from "react";

import { getMyInfo } from "../api";

export default function Home({ auth }) {
  const [me, setMe] = useState(null);

  useEffect(() => {
    let mounted = true;

    async function load() {
      if (!auth.authenticated) {
        setMe(null);
        return;
      }

      try {
        const data = await getMyInfo();
        if (mounted) {
          setMe(data);
        }
      } catch {
        if (mounted) {
          setMe(null);
        }
      }
    }

    load();
    return () => {
      mounted = false;
    };
  }, [auth.authenticated]);

  return (
    <section>
      <h2>Home</h2>
      <p>학생은 수강 후 재생 가능하고, 관리자는 강의 생성 및 영상 업로드를 수행합니다.</p>
      {!auth.authenticated && <p>로그인 후 기능을 사용할 수 있습니다.</p>}
      {me && <pre>{JSON.stringify(me, null, 2)}</pre>}
    </section>
  );
}
