import { useState } from 'react';
import { InputModal, AlertModal } from './Modal.jsx';

const BASE_ITEMS = [
  { name: 'Silver Tea Set', src: '/Silverware.png' },
  { name: 'Iron Knife', src: '/Knife.png' },
  { name: 'Imported Mirror', src: '/Mirror.png' },
  { name: 'Glass Beads', src: '/GlassBeads.png' },
];

export function initBarterItems() {
  return BASE_ITEMS.map(item => ({
    ...item,
    price: Math.floor(Math.random() * 10) + 1,
  }));
}

export default function BarteringTask({ items, onSort, onComplete, onFail }) {
  const [selectedItem, setSelectedItem] = useState(null);
  const [alertMsg, setAlertMsg] = useState(null);

  function handleItemClick(item) {
    setSelectedItem(item);
  }

  function handleBarter(input) {
    if (!selectedItem) return;
    const price = parseInt(input, 10);
    if (isNaN(price)) {
      setAlertMsg('Invalid input — please enter a valid number.');
      setSelectedItem(null);
      return;
    }
    if (price === selectedItem.price) {
      setAlertMsg(`Successful trade! The trader sold the ${selectedItem.name} for ${selectedItem.price} furs.`);
      setSelectedItem(null);
      // complete after alert dismissed
      setTimeout(() => onComplete(), 0);
    } else if (price > selectedItem.price) {
      setAlertMsg("This is clearly WORTH LESS than what you're offering! Are you trying to scam me? Go away!");
      setSelectedItem(null);
      onFail();
    } else {
      setAlertMsg("How arrogant! This is clearly WORTH MORE than what you're offering! Are you trying to scam me? Go away!");
      setSelectedItem(null);
      onFail();
    }
  }

  return (
    <div className="bartering-task">
      <img src="/Shelf.png" alt="shelf" className="barter-shelf-bg" />
      <div className="barter-hud">
        <strong>TASK: Make 1 successful trade with the traders!</strong>
      </div>
      <div className="barter-items">
        {items.map(item => (
          <div key={item.name} className="barter-item" onClick={() => handleItemClick(item)}>
            <img src={item.src} alt={item.name} />
            <div className="barter-item-name">{item.name}</div>
          </div>
        ))}
      </div>
      <div className="barter-sort-btns">
        <img src="/AscSort.png" alt="Sort A-Z" onClick={() => onSort(false)} title="Sort A→Z" />
        <img src="/DescSort.png" alt="Sort Z-A" onClick={() => onSort(true)} title="Sort Z→A" />
      </div>

      {selectedItem && (
        <InputModal
          title={`Barter for ${selectedItem.name}`}
          message={`Enter the number of beaver furs to barter for the ${selectedItem.name}:`}
          imageUrl="/Beaver.jpg"
          onConfirm={handleBarter}
          onCancel={() => setSelectedItem(null)}
        />
      )}
      {alertMsg && (
        <AlertModal message={alertMsg} onClose={() => setAlertMsg(null)} />
      )}
    </div>
  );
}
