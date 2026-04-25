import {useState} from "react";
import {Input} from "@/presentation/ui/input/Input.tsx";
import {Button} from "@/presentation/ui/button/Button.tsx";
import styles from "./UserSearch.module.css"
interface Props {
    onSearch: (id: number) => Promise<void>
    loading: boolean
}

export const UserSearch = ({ onSearch, loading }: Props) => {
    const [userId, setUserId] = useState("")

    const handleSearch = async () => {
        if (!userId) return
        await onSearch(Number(userId))
    }

    return (
        <section className={styles.container}>

            <h3 className={styles.title}>
                Пошук користувача
            </h3>

            <div className={styles.form}>

                <Input
                    label="User ID"
                    type="number"
                    value={userId}
                    onChange={(e) => setUserId(e.target.value)}
                />

                <Button
                    onClick={handleSearch}
                    loading={loading}
                >
                    Знайти
                </Button>

            </div>

        </section>
    )
}