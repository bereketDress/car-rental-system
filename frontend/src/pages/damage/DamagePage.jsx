import { useEffect, useState } from "react";
import { damageService } from "../../services/damageService";
import DamageTable from "../../components/damage/DamageTable";

export default function DamagesPage() {
    const [damages, setDamages] = useState([]);

    const loadDamages = async () => {
        const res = await damageService.listAll();
        setDamages(res.data);
    };

    useEffect(() => {
        loadDamages();
    }, []);

    const updateStatus = async (id, status) => {
        await damageService.updateStatus(id, status);
        loadDamages();
    };

    return (
        <div className="mx-auto max-w-6xl p-6">
            <h1 className="mb-6 text-3xl font-bold text-gray-900">
                Damage Reports
            </h1>

            <DamageTable
                damages={damages}
                onStatusChange={updateStatus}
            />
        </div>
    );
}
