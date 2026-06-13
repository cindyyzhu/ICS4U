import { useState } from 'react';

export function AlertModal({ message, onClose }) {
  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <p>{message}</p>
        <button className="btn" onClick={onClose}>OK</button>
      </div>
    </div>
  );
}

export function InputModal({ title, message, imageUrl, onConfirm, onCancel }) {
  const [value, setValue] = useState('');
  return (
    <div className="modal-backdrop">
      <div className="modal">
        {imageUrl && <img src={imageUrl} alt="" />}
        <h2>{title}</h2>
        <p>{message}</p>
        <input
          type="number"
          value={value}
          onChange={e => setValue(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && onConfirm(value)}
          autoFocus
          placeholder="Enter number..."
        />
        <div className="btn-row" style={{ justifyContent: 'center' }}>
          <button className="btn" onClick={() => onConfirm(value)}>Barter</button>
          <button className="btn" onClick={onCancel}>Cancel</button>
        </div>
      </div>
    </div>
  );
}
