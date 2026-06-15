import { branchService } from "../../services/api";

export default function BranchList({branches, onEdit, reload,}) {
    const remove = async (id) => {
        await branchService.delete(id);
        reload();
    };

    return (
        <div className="grid gap-4 md:grid-cols-3">
            {branches.map((branch) => (
                <div
                    key={branch.branchId}
                    className="rounded border p-4"
                >
                    <h2 className="font-bold">
                        {branch.name}
                    </h2>

                    <p>{branch.location}</p>

                    <div className="mt-3 flex gap-2">
                        <button
                            onClick={() => onEdit(branch)}
                            className="rounded border px-3 py-1"
                        >
                            Edit
                        </button>

                        <button
                            onClick={() => remove(branch.branchId)}
                            className="rounded bg-red-600 px-3 py-1 text-white"
                        >
                            Delete
                        </button>
                    </div>
                </div>
            ))}
        </div>
    );
}
