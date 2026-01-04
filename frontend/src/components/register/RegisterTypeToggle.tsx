import {Button} from "@/components/button/Button.tsx";

interface Props {
    isBusiness: boolean
    onChange: (value: boolean) => void
}
export function RegisterTypeToggle({isBusiness, onChange}: Props){
    return(
        <div>
            <Button
            type="button"
            disabled={!isBusiness}
            onClick={()=>onChange(false)}
            >
                Фізична особа
            </Button>
            <Button
                type="button"
                disabled={isBusiness}
                onClick={()=>onChange(true)}
            >
                Бізнес-клієнт
            </Button>
        </div>
    )
}