import { useEffect, useState } from "react";
import { branchService } from "../../services/api";

const empty = {
    name: "",
    location: "",
    phone: "",
};

export default function BranchForm({editing, reload, clearEdit,}) {
    const [form, setForm] = useState(empty);

    useEffect(() => {
        setForm(editing || empty);
    }, [editing]);

    const save = async (e) => {
        e.preventDefault();

        if (editing) {
            await branchService.update(editing.branchId, form);
        } else {
            await branchService.create(form);
        }

        setForm(empty);
        clearEdit();
        reload();
    };

    return (
        <form onSubmit={save} className="mb-6 flex gap-2">
            <input
                className="border p-2"
                placeholder="Name"
                value={form.name}
                onChange={(e) =>
                    setForm({ ...form, name: e.target.value })
                }
            />

            <input
                className="border p-2"
                placeholder="Location"
                value={form.location}
                onChange={(e) =>
                    setForm({ ...form, location: e.target.value })
                }
            />

            <button className="rounded bg-blue-600 px-4 text-white">
                {editing ? "Update" : "Add"}
            </button>
        </form>
    );
}
