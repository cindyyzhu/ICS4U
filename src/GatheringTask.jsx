import { useRef, useEffect, useCallback } from 'react';

const MATERIALS = [
  { name: 'Wood', src: '/Wood.png', x: 200, y: 400 },
  { name: 'Sinew', src: '/Sinew.png', x: 300, y: 200 },
  { name: 'Sealskin', src: '/Sealskin.png', x: 400, y: 300 },
  { name: 'Copper', src: '/Copper.png', x: 500, y: 400 },
  { name: 'Bone', src: '/Bone.png', x: 600, y: 200 },
];

const ITEM_SIZE = 50;

export default function GatheringTask({ remaining, taskTimer, timeLimit, onGather }) {
  const canvasRef = useRef(null);
  const imagesRef = useRef({});

  useEffect(() => {
    MATERIALS.forEach(m => {
      if (!imagesRef.current[m.src]) {
        const img = new Image();
        img.src = m.src;
        imagesRef.current[m.src] = img;
      }
    });
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, 800, 600);

    // HUD
    ctx.fillStyle = 'rgba(255,255,255,0.88)';
    ctx.fillRect(0, 0, 260, 80);
    ctx.fillStyle = '#000';
    ctx.font = '14px Georgia';
    ctx.fillText('TASK: Gather materials for a bow!', 10, 20);
    ctx.fillText('Score: ' + (5 - remaining.length) + ' / 5', 10, 40);
    ctx.fillText('Time remaining: ' + (timeLimit - taskTimer) + 's', 10, 60);

    // draw materials
    remaining.forEach(m => {
      const img = imagesRef.current[m.src];
      if (img && img.complete) {
        ctx.drawImage(img, m.x, m.y, ITEM_SIZE, ITEM_SIZE);
      } else {
        ctx.fillStyle = '#a85';
        ctx.fillRect(m.x, m.y, ITEM_SIZE, ITEM_SIZE);
      }
      ctx.fillStyle = '#000';
      ctx.font = '11px Georgia';
      ctx.fillText(m.name, m.x, m.y + ITEM_SIZE + 12);
    });
  }, [remaining, taskTimer, timeLimit]);

  const handleClick = useCallback((e) => {
    const canvas = canvasRef.current;
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    for (const m of remaining) {
      if (x >= m.x && x <= m.x + ITEM_SIZE + 10 && y >= m.y && y <= m.y + ITEM_SIZE + 10) {
        onGather(m.name);
        break;
      }
    }
  }, [remaining, onGather]);

  return (
    <canvas
      ref={canvasRef}
      width={800}
      height={600}
      className="canvas-task"
      onClick={handleClick}
    />
  );
}

export { MATERIALS, ITEM_SIZE };
