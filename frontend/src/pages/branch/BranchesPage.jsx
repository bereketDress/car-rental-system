import { useEffect, useState } from "react";
import { branchService } from "../../services/branchService";
import BranchForm from "../../components/branch/BranchForm";
import BranchList from "../../components/branch/BranchList";

export default function BranchesPage() {
    const [branches, setBranches] = useState([]);
    const [editing, setEditing] = useState(null);

    const loadBranches = async () => {
        const res = await branchService.listAll();
        setBranches(res.data);
    };

    useEffect(() => {
        loadBranches();
    }, []);

    return (
        <div className="p-6">
            <h1 className="mb-6 text-3xl font-bold">
                Manage Branches
            </h1>

            <BranchForm
                editing={editing}
                reload={loadBranches}
                clearEdit={() => setEditing(null)}
            />

            <BranchList
                branches={branches}
                onEdit={setEditing}
                reload={loadBranches}
            />
        </div>
    );
}
