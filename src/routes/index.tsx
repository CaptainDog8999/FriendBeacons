import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/")({ component: Home });

function Home() {
  return (
    <main className="p-8">
      <h1 className="text-2xl font-semibold">FriendBeacons</h1>
      <p className="mt-2 text-zinc-500">Hello</p>
    </main>
  );
}
