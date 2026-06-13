import { useRef, useEffect, useCallback } from 'react';

const CHECKPOINTS = [
  { id: 'C1', x: 200, y: 100, label: 'Elder', imgSrc: '/Elder.png' },
  { id: 'C2', x: 300, y: 200, label: 'Uncle', imgSrc: '/Basket.png' },
  { id: 'C3', x: 400, y: 300, label: 'Father', imgSrc: '/Bow.png' },
  { id: 'C4', x: 500, y: 400, label: 'Trade Post', imgSrc: '/Trade.png' },
];

export { CHECKPOINTS };

const IMG_SIZE = 80;
const PLAYER_W = 50;
const PLAYER_H = 60;

export default function GameMap({ playerX, playerY, completed, onPlayerMove, onCheckpointEnter, failureCount }) {
  const canvasRef = useRef(null);
  const imagesRef = useRef({});
  const playerImgRef = useRef(null);
  const bgImgRef = useRef(null);

  useEffect(() => {
    const bg = new Image(); bg.src = '/Village.jpg'; bgImgRef.current = bg;
    const player = new Image(); player.src = '/Mato.png'; playerImgRef.current = player;
    CHECKPOINTS.forEach(cp => {
      const img = new Image(); img.src = cp.imgSrc; imagesRef.current[cp.id] = img;
    });
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, 800, 600);

    if (bgImgRef.current?.complete) ctx.drawImage(bgImgRef.current, 0, 0, 800, 600);

    // draw checkpoints
    CHECKPOINTS.forEach(cp => {
      if (completed.includes(cp.id)) return;
      const img = imagesRef.current[cp.id];
      if (img?.complete) ctx.drawImage(img, cp.x, cp.y, IMG_SIZE, IMG_SIZE);

      ctx.font = '13px Georgia';
      const tw = ctx.measureText(cp.label).width;
      const tx = cp.x + (IMG_SIZE - tw) / 2;
      const ty = cp.y + IMG_SIZE + 16;
      ctx.fillStyle = 'rgba(255,255,255,0.9)';
      ctx.fillRect(tx - 8, ty - 14, tw + 16, 18);
      ctx.fillStyle = '#000';
      ctx.fillText(cp.label, tx, ty);
    });

    // draw player
    if (playerImgRef.current?.complete) {
      ctx.drawImage(playerImgRef.current, playerX, playerY, PLAYER_W, PLAYER_H);
    } else {
      ctx.fillStyle = '#3355cc';
      ctx.fillRect(playerX, playerY, 20, 30);
    }

    // HUD
    ctx.fillStyle = 'rgba(255,255,255,0.85)';
    ctx.fillRect(0, 0, 180, 50);
    ctx.fillStyle = '#c00';
    ctx.font = '13px Georgia';
    ctx.fillText(`Failures: ${failureCount} / 3`, 10, 30);
  }, [playerX, playerY, completed, failureCount]);

  // keyboard movement
  useEffect(() => {
    const handler = (e) => {
      const step = 10;
      let dx = 0, dy = 0;
      if (e.key === 'ArrowLeft') dx = -step;
      else if (e.key === 'ArrowRight') dx = step;
      else if (e.key === 'ArrowUp') dy = -step;
      else if (e.key === 'ArrowDown') dy = step;
      else return;
      e.preventDefault();
      onPlayerMove(dx, dy);
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [onPlayerMove]);

  // check checkpoint proximity whenever player moves
  useEffect(() => {
    for (const cp of CHECKPOINTS) {
      if (completed.includes(cp.id)) continue;
      if (
        playerX >= cp.x - 20 && playerX <= cp.x + IMG_SIZE + 20 &&
        playerY >= cp.y - 20 && playerY <= cp.y + IMG_SIZE + 20
      ) {
        onCheckpointEnter(cp.id);
        break;
      }
    }
  }, [playerX, playerY, completed, onCheckpointEnter]);

  const handleClick = useCallback((e) => {
    const canvas = canvasRef.current;
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    for (const cp of CHECKPOINTS) {
      if (completed.includes(cp.id)) continue;
      if (x >= cp.x && x <= cp.x + IMG_SIZE && y >= cp.y && y <= cp.y + IMG_SIZE) {
        onCheckpointEnter(cp.id);
        return;
      }
    }
  }, [completed, onCheckpointEnter]);

  return (
    <canvas
      ref={canvasRef}
      width={800}
      height={600}
      style={{ position: 'absolute', inset: 0, cursor: 'default' }}
      onClick={handleClick}
      tabIndex={0}
    />
  );
}
