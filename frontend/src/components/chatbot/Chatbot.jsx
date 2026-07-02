// frontend/src/components/chatbot/Chatbot.jsx

import { useState } from "react";
import { request } from "../../services/apiClient";

export default function Chatbot() {
    const [message, setMessage] = useState("");
    const [reply, setReply] = useState("");
    const [error, setError] = useState("");
    const [sending, setSending] = useState(false);

    async function sendMessage() {
        const text = message.trim();
        if (!text || sending) {
            return;
        }

        setSending(true);
        setError("");

        try {
            const { data } = await request("/api/chatbot", {
                method: "POST",
                body: JSON.stringify({ message: text }),
            });

            setReply(data.reply);
            setMessage("");
        } catch {
            setError("Unable to reach chatbot.");
        } finally {
            setSending(false);
        }
    }

    return (
        <aside className="fixed bottom-5 right-5 z-50 w-[min(22rem,calc(100vw-2rem))] rounded-lg border border-gray-200 bg-white p-4 shadow-lg">
            <h3 className="mb-3 text-base font-semibold text-gray-900">Chatbot</h3>

            <div className="flex gap-2">
                <input
                    className="min-w-0 flex-1 rounded-md border border-gray-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === "Enter") {
                            sendMessage();
                        }
                    }}
                    placeholder="Ask something..."
                />

                <button
                    className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-gray-300"
                    disabled={sending || !message.trim()}
                    onClick={sendMessage}
                    type="button"
                >
                    {sending ? "Sending" : "Send"}
                </button>
            </div>

            {reply && <p className="mt-3 text-sm leading-6 text-gray-700">{reply}</p>}
            {error && <p className="mt-3 text-sm text-red-600">{error}</p>}
        </aside>
    );
}
