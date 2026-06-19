import { useEffect, useState } from "react";
import { branchService } from "../../services/branchService";

const empty = {
    name: "",
    phone: "",
    city: "",
    street: "",
    zipcode: "",
};

export default function BranchForm({editing, reload, clearEdit,}) {
    const [form, setForm] = useState(empty);

    useEffect(() => {
        setForm(editing ? { ...empty, ...editing } : empty);
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
        <form onSubmit={save} className="mb-6 grid gap-2 md:grid-cols-5">
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
                placeholder="Phone"
                value={form.phone}
                onChange={(e) =>
                    setForm({ ...form, phone: e.target.value })
                }
            />

            <input
                className="border p-2"
                placeholder="City"
                value={form.city}
                onChange={(e) =>
                    setForm({ ...form, city: e.target.value })
                }
            />

            <input
                className="border p-2"
                placeholder="Street"
                value={form.street}
                onChange={(e) =>
                    setForm({ ...form, street: e.target.value })
                }
            />

            <input
                className="border p-2"
                placeholder="Zipcode"
                value={form.zipcode}
                onChange={(e) =>
                    setForm({ ...form, zipcode: e.target.value })
                }
            />

            <button className="rounded bg-blue-600 px-4 text-white">
                {editing ? "Update" : "Add"}
            </button>
        </form>
    );
}
