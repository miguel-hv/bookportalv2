type InputFieldProps <T extends string = string> = {
  label: string;
  type?: string;
  value: T;
  onChange: (val: T) => void;
  required?: boolean;
};

export default function InputField<T extends string = string>(
  { label, type = 'text', value, onChange, required = false }: InputFieldProps<T>
) {
  return (
    <div>
      <label className="block text-sm font-medium mb-1">{label}</label>
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value as T)}
        required={required}
        className="w-full border rounded-lg px-3 py-2"
      />
    </div>
  );
}
