import { useState, useEffect, useRef, useCallback } from 'react';
import GameMap, { CHECKPOINTS } from './GameMap.jsx';
import MusicTask from './MusicTask.jsx';
import GatheringTask, { MATERIALS } from './GatheringTask.jsx';
import HuntingTask from './HuntingTask.jsx';
import BarteringTask, { initBarterItems } from './BarteringTask.jsx';
import { AlertModal } from './Modal.jsx';
import './index.css';

const TASK_TIME = 10;
const MAX_FAILS = 3;
const W = 800, H = 600;

// Screens: intro | instructions | game | end | failed
function useGameState() {
  const [screen, setScreen] = useState('intro');
  const [playerX, setPlayerX] = useState(50);
  const [playerY, setPlayerY] = useState(50);
  const [failureCount, setFailureCount] = useState(0);
  const [completed, setCompleted] = useState([]);
  const [activeTask, setActiveTask] = useState(null); // null | 'C1'..'C4'
  const [showDialogue, setShowDialogue] = useState(null);
  const [alert, setAlert] = useState(null);

  // Music
  const [noteIndex, setNoteIndex] = useState(0);

  // Gathering
  const [gatherRemaining, setGatherRemaining] = useState([...MATERIALS]);

  // Hunting
  const [animals, setAnimals] = useState([]);
  const animalTimerRef = useRef(null);

  // Bartering
  const [barterItems, setBarterItems] = useState([]);

  // Task timer
  const [taskTimer, setTaskTimer] = useState(0);
  const taskTimerRef = useRef(null);

  const clearTaskTimer = () => {
    if (taskTimerRef.current) { clearInterval(taskTimerRef.current); taskTimerRef.current = null; }
  };
  const startTaskTimer = () => {
    clearTaskTimer();
    taskTimerRef.current = setInterval(() => {
      setTaskTimer(t => t + 1);
    }, 1000);
  };

  // watch taskTimer for time-out (only for gathering/hunting)
  useEffect(() => {
    if ((activeTask === 'C2' || activeTask === 'C3') && taskTimer > TASK_TIME) {
      clearTaskTimer();
      if (animalTimerRef.current) { clearInterval(animalTimerRef.current); animalTimerRef.current = null; }
      setActiveTask(null);
      setFailureCount(f => f + 1);
      setAlert("Time's up! You failed the task :( Try again.");
    }
  }, [taskTimer, activeTask]);

  // stop animal timer when not hunting
  useEffect(() => {
    if (activeTask !== 'C3' && animalTimerRef.current) {
      clearInterval(animalTimerRef.current);
      animalTimerRef.current = null;
    }
  }, [activeTask]);

  useEffect(() => {
    return () => { clearTaskTimer(); if (animalTimerRef.current) clearInterval(animalTimerRef.current); };
  }, []);

  function startTask(cpId) {
    setTaskTimer(0);
    setActiveTask(cpId);
    if (cpId === 'C1') {
      setNoteIndex(0);
    } else if (cpId === 'C2') {
      setGatherRemaining([...MATERIALS]);
      startTaskTimer();
    } else if (cpId === 'C3') {
      const newAnimals = Array.from({ length: 5 }, (_, i) => ({
        id: i,
        x: Math.floor(Math.random() * (W - 80)) + 10,
        y: Math.floor(Math.random() * (H - 80)) + 10,
      }));
      setAnimals(newAnimals);
      startTaskTimer();
      animalTimerRef.current = setInterval(() => {
        setAnimals(prev => prev.map(a => ({
          ...a,
          x: Math.max(0, Math.min(a.x + Math.floor(Math.random() * 41) - 20, W - 60)),
          y: Math.max(0, Math.min(a.y + Math.floor(Math.random() * 41) - 20, H - 60)),
        })));
      }, 400);
    } else if (cpId === 'C4') {
      setBarterItems(initBarterItems());
    }
  }

  function completeTask(cpId) {
    clearTaskTimer();
    if (animalTimerRef.current) { clearInterval(animalTimerRef.current); animalTimerRef.current = null; }
    setActiveTask(null);
    setTaskTimer(0);
    const newCompleted = [...completed, cpId];
    setCompleted(newCompleted);
    setAlert('Congratulations! You completed the task successfully.');
    // check end
    if (newCompleted.length === 4) {
      setTimeout(() => {
        if (failureCount >= MAX_FAILS) setScreen('failed');
        else setScreen('end');
      }, 500);
    }
  }

  function fail() {
    setFailureCount(f => f + 1);
  }

  function enterCheckpoint(cpId) {
    if (activeTask || completed.includes(cpId)) return;
    setShowDialogue(cpId);
  }

  function dismissDialogue() {
    if (!showDialogue) return;
    const cp = showDialogue;
    setShowDialogue(null);
    startTask(cp);
  }

  function movePlayer(dx, dy) {
    if (activeTask || showDialogue) return;
    setPlayerX(x => Math.max(0, Math.min(x + dx, W - 50)));
    setPlayerY(y => Math.max(0, Math.min(y + dy, H - 60)));
  }

  // Music handlers
  const NOTES = ['a','b','c','d','e','f','g'];
  function playNote(key) {
    const audio = new Audio(`/${key}.wav`);
    audio.play().catch(() => {});
  }
  function handleMusicKey(key) {
    if (activeTask !== 'C1') return;
    const expected = NOTES[noteIndex];
    if (key === expected) {
      playNote(key);
      const next = noteIndex + 1;
      if (next >= NOTES.length) {
        setNoteIndex(0);
        completeTask('C1');
      } else {
        setNoteIndex(next);
      }
    } else {
      setAlert('You played the wrong note! Try again.');
      setNoteIndex(0);
      setFailureCount(f => f + 1);
    }
  }

  function handleGather(name) {
    const next = gatherRemaining.filter(m => m.name !== name);
    setGatherRemaining(next);
    if (next.length === 0) completeTask('C2');
  }

  function handleHunt(id) {
    const next = animals.filter(a => a.id !== id);
    setAnimals(next);
    if (next.length === 0) completeTask('C3');
  }

  function handleBarterSort(desc) {
    setBarterItems(prev => [...prev].sort((a, b) =>
      desc ? b.name.localeCompare(a.name) : a.name.localeCompare(b.name)
    ));
  }

  function resetGame() {
    clearTaskTimer();
    if (animalTimerRef.current) { clearInterval(animalTimerRef.current); animalTimerRef.current = null; }
    setScreen('intro');
    setPlayerX(50); setPlayerY(50);
    setFailureCount(0); setCompleted([]);
    setActiveTask(null); setShowDialogue(null);
    setNoteIndex(0); setGatherRemaining([...MATERIALS]);
    setAnimals([]); setBarterItems([]);
    setTaskTimer(0); setAlert(null);
  }

  return {
    screen, setScreen,
    playerX, playerY, movePlayer,
    failureCount, completed,
    activeTask, showDialogue, dismissDialogue,
    enterCheckpoint,
    alert, setAlert,
    noteIndex, handleMusicKey,
    gatherRemaining, handleGather,
    animals, handleHunt,
    barterItems, handleBarterSort,
    taskTimer,
    completeTask,
    fail,
    resetGame,
  };
}

export default function App() {
  const g = useGameState();

  // Focus game container for key events
  const containerRef = useRef(null);
  useEffect(() => {
    if (g.screen === 'game') containerRef.current?.focus();
  }, [g.screen, g.activeTask]);

  return (
    <>
    <div className="game-container" ref={containerRef} tabIndex={-1} style={{ outline: 'none' }}>
      {/* INTRO */}
      {g.screen === 'intro' && (
        <div className="screen">
          <img src="/Village-Intro.png" alt="village" className="screen-bg" />
          <div className="screen-content">
            <div className="intro-title">Welcome to Mato's<br />Fur Trade Adventure!</div>
            <div style={{ flex: 1 }} />
            <button className="btn" style={{ marginBottom: 100 }} onClick={() => g.setScreen('instructions')}>
              Play!
            </button>
          </div>
        </div>
      )}

      {/* INSTRUCTIONS */}
      {g.screen === 'instructions' && (
        <div className="screen">
          <img src="/Village-Intro.png" alt="village" className="screen-bg" />
          <div className="screen-content" style={{ alignItems: 'center', justifyContent: 'center' }}>
            <img
              src="/Instruction-text.png"
              alt="instructions"
              style={{ width: 600, height: 350, objectFit: 'contain' }}
            />
            <button className="btn" style={{ marginTop: 20 }} onClick={() => g.setScreen('game')}>
              Got It!
            </button>
          </div>
        </div>
      )}

      {/* GAME */}
      {g.screen === 'game' && (
        <div style={{ position: 'relative', width: 800, height: 600 }}>
          {/* Base map — always rendered */}
          <GameMap
            playerX={g.playerX}
            playerY={g.playerY}
            completed={g.completed}
            onPlayerMove={g.movePlayer}
            onCheckpointEnter={g.enterCheckpoint}
            failureCount={g.failureCount}
          />

          {/* Dialogue overlay */}
          {g.showDialogue && (
            <div className="dialogue-overlay" onClick={g.dismissDialogue} title="Click to start task">
              <img src={`/${g.showDialogue}.png`} alt="dialogue" />
              <div style={{
                position: 'absolute', bottom: 20, left: 0, right: 0,
                textAlign: 'center', color: '#fff',
                fontSize: 15, textShadow: '1px 1px 3px #000',
              }}>
                Click anywhere to start the task
              </div>
            </div>
          )}

          {/* Music task */}
          {g.activeTask === 'C1' && (
            <MusicTask noteIndex={g.noteIndex} onKeyNote={g.handleMusicKey} />
          )}

          {/* Gathering task */}
          {g.activeTask === 'C2' && (
            <GatheringTask
              remaining={g.gatherRemaining}
              taskTimer={g.taskTimer}
              timeLimit={TASK_TIME}
              onGather={g.handleGather}
            />
          )}

          {/* Hunting task */}
          {g.activeTask === 'C3' && (
            <HuntingTask
              animals={g.animals}
              taskTimer={g.taskTimer}
              timeLimit={TASK_TIME}
              onHunt={g.handleHunt}
            />
          )}

          {/* Bartering task */}
          {g.activeTask === 'C4' && (
            <BarteringTask
              items={g.barterItems}
              onSort={g.handleBarterSort}
              onComplete={() => g.completeTask('C4')}
              onFail={g.fail}
            />
          )}

          {/* Failures HUD */}
          {!g.activeTask && !g.showDialogue && g.failureCount > 0 && (
            <div className="hud" style={{ top: 'auto', bottom: 8 }}>
              ⚠️ Failures: {g.failureCount} / {MAX_FAILS}
            </div>
          )}

          {/* Completed checkpoints indicator */}
          {!g.activeTask && !g.showDialogue && (
            <div style={{
              position: 'absolute', top: 8, right: 8,
              background: 'rgba(255,255,255,0.85)', padding: '6px 10px',
              borderRadius: 6, fontSize: 13, zIndex: 10,
            }}>
              Progress: {g.completed.length} / 4
            </div>
          )}
        </div>
      )}

      {/* SUCCESS END */}
      {g.screen === 'end' && (
        <div className="screen">
          <img src="/Village-Intro.png" alt="village" className="screen-bg" />
          <div className="end-screen-text">
            <img src="/endScenePanelText.png" alt="success" />
          </div>
          <div className="end-screen-btns">
            <button className="btn" onClick={g.resetGame}>Play Again!</button>
            <button className="btn" onClick={() => window.close()}>Exit Game</button>
          </div>
        </div>
      )}

      {/* FAILED END */}
      {g.screen === 'failed' && (
        <div className="screen">
          <img src="/Village-Intro.png" alt="village" className="screen-bg" />
          <div className="end-screen-text">
            <img src="/failedEndSceneText.png" alt="failed" />
          </div>
          <div className="end-screen-btns">
            <button className="btn" onClick={g.resetGame}>Try again...</button>
          </div>
        </div>
      )}

      {/* Global alert */}
      {g.alert && (
        <AlertModal message={g.alert} onClose={() => g.setAlert(null)} />
      )}
    </div>
    <footer className="footer">
      Made with <span className="heart">♥</span> by Cindy Zhu 2026
    </footer>
    </>
  );
}