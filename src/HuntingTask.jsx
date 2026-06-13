import { useRef, useEffect, useCallback } from 'react';

const BEAVER_SIZE = 50;

export default function HuntingTask({ animals, taskTimer, timeLimit, onHunt }) {
  const canvasRef = useRef(null);
  const beaverImgRef = useRef(null);

  useEffect(() => {
    const img = new Image();
    img.src = '/beaver.png';
    beaverImgRef.current = img;
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, 800, 600);

    ctx.fillStyle = 'rgba(255,255,255,0.88)';
    ctx.fillRect(0, 0, 220, 80);
    ctx.fillStyle = '#000';
    ctx.font = '14px Georgia';
    ctx.fillText('TASK: Hunt 5 beavers!', 10, 20);
    ctx.fillText('Score: ' + (5 - animals.length) + ' / 5', 10, 40);
    ctx.fillText('Time remaining: ' + (timeLimit - taskTimer) + 's', 10, 60);

    animals.forEach(a => {
      const img = beaverImgRef.current;
      if (img && img.complete) {
        ctx.drawImage(img, a.x, a.y, BEAVER_SIZE, BEAVER_SIZE);
      } else {
        ctx.fillStyle = '#8b4513';
        ctx.fillRect(a.x, a.y, BEAVER_SIZE, BEAVER_SIZE);
      }
    });
  }, [animals, taskTimer, timeLimit]);

  const handleClick = useCallback((e) => {
    const canvas = canvasRef.current;
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    for (const a of animals) {
      if (x >= a.x && x <= a.x + BEAVER_SIZE && y >= a.y && y <= a.y + BEAVER_SIZE) {
        onHunt(a.id);
        break;
      }
    }
  }, [animals, onHunt]);

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
