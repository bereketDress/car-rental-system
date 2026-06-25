import { useEffect, useState } from "react";
import { branchService } from "../../services/branchService";
import { staffService } from "../../services/staffService";

const emptyStaff = {
    name: "",
    email: "",
    phone: "",
    branchId: "",
};

function toPayload(form) {
    return {
        name: form.name,
        email: form.email,
        phone: form.phone,
        role: "STAFF",
        branch: form.branchId ? { branchId: Number(form.branchId) } : null,
    };
}

function fromStaff(staff) {
    return {
        name: staff.name ?? "",
        email: staff.email ?? "",
        phone: staff.phone ?? "",
        branchId: staff.branch?.branchId ? String(staff.branch.branchId) : "",
    };
}

export default function StaffPage() {
    const [staff, setStaff] = useState([]);
    const [branches, setBranches] = useState([]);
    const [editing, setEditing] = useState(null);
    const [form, setForm] = useState(emptyStaff);
    const [message, setMessage] = useState("");

    const loadData = async () => {
        const [staffRes, branchRes] = await Promise.all([
            staffService.listAll(),
            branchService.listAll(),
        ]);

        setStaff(staffRes.data);
        setBranches(branchRes.data);
    };

    useEffect(() => {
        loadData();
    }, []);

    const updateField = (field, value) => {
        setForm((current) => ({ ...current, [field]: value }));
    };

    const edit = (nextStaff) => {
        setEditing(nextStaff);
        setForm(fromStaff(nextStaff));
        setMessage("");
    };

    const clear = () => {
        setEditing(null);
        setForm(emptyStaff);
    };

    const save = async (e) => {
        e.preventDefault();
        setMessage("");

        try {
            if (editing) {
                await staffService.update(editing.staffId, toPayload(form));
                setMessage("Staff member updated.");
            } else {
                const response = await staffService.create(toPayload(form));
                setMessage(
                    response.data?.temporaryPassword
                        ? `Staff member added. Temporary password: ${response.data.temporaryPassword}`
                        : "Staff member added."
                );
            }

            clear();
            await loadData();
        } catch (error) {
            setMessage(error.message || "Staff save failed.");
        }
    };

    const remove = async (staffId) => {
        setMessage("");

        try {
            await staffService.delete(staffId);
            setMessage("Staff member deleted.");
            await loadData();
        } catch (error) {
            setMessage(error.message || "Staff delete failed.");
        }
    };

    return (
        <div className="p-6">
            <h1 className="mb-6 text-3xl font-bold">Manage Staff</h1>

            {message && (
                <p className="mb-4 rounded bg-gray-50 p-3 text-gray-700">{message}</p>
            )}

            <form onSubmit={save} className="mb-6 grid gap-2 md:grid-cols-4">
                <input
                    value={form.name}
                    onChange={(e) => updateField("name", e.target.value)}
                    placeholder="Name"
                    className="border p-2"
                    required
                />

                <input
                    value={form.email}
                    onChange={(e) => updateField("email", e.target.value)}
                    type="email"
                    placeholder="Email"
                    className="border p-2"
                    required
                />

                <input
                    value={form.phone}
                    onChange={(e) => updateField("phone", e.target.value)}
                    placeholder="Phone"
                    className="border p-2"
                />

                <select
                    value={form.branchId}
                    onChange={(e) => updateField("branchId", e.target.value)}
                    className="border p-2"
                >
                    <option value="">No branch</option>
                    {branches.map((branch) => (
                        <option key={branch.branchId} value={branch.branchId}>
                            {branch.name}
                        </option>
                    ))}
                </select>

                <div className="flex gap-2 md:col-span-4">
                    <button className="rounded bg-blue-600 px-4 py-2 text-white">
                        {editing ? "Update Staff" : "Add Staff"}
                    </button>

                    {editing && (
                        <button
                            type="button"
                            onClick={clear}
                            className="rounded border px-4 py-2"
                        >
                            Cancel
                        </button>
                    )}
                </div>
            </form>

            <div className="grid gap-4 md:grid-cols-3">
                {staff.map((member) => (
                    <div key={member.staffId} className="rounded border p-4">
                        <h2 className="font-bold">{member.name}</h2>
                        <p>{member.email}</p>
                        <p>{member.phone}</p>
                        <p>{member.branch?.name || "No branch"}</p>

                        <div className="mt-3 flex gap-2">
                            <button
                                onClick={() => edit(member)}
                                className="rounded border px-3 py-1"
                            >
                                Edit
                            </button>

                            <button
                                onClick={() => remove(member.staffId)}
                                className="rounded bg-red-600 px-3 py-1 text-white hover:bg-red-700"
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
