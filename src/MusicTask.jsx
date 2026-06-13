import { useEffect, useRef } from 'react';

const NOTES = ['a', 'b', 'c', 'd', 'e', 'f', 'g'];

export default function MusicTask({ noteIndex, onKeyNote }) {
  const containerRef = useRef(null);

  useEffect(() => {
    const handler = (e) => {
      const key = e.key.toLowerCase();
      if (NOTES.includes(key)) onKeyNote(key);
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [onKeyNote]);

  return (
    <div className="music-task" ref={containerRef}>
      <div className="music-task-inner">
        <h2>TASK: Play the notes in order: a, b, c, d, e, f, g</h2>
        <p style={{ fontSize: 13, color: '#555', marginBottom: 6 }}>Press the keys on your keyboard!</p>
        <img src="/MusicalNotes.png" alt="musical notes" className="music-notes-img" />
        <div className="note-indicators">
          {NOTES.map((note, i) => (
            <div
              key={note}
              className={`note-dot ${i < noteIndex ? 'done' : i === noteIndex ? 'active' : ''}`}
            >
              {note.toUpperCase()}
            </div>
          ))}
        </div>
        <p style={{ marginTop: 12, fontSize: 13, color: '#666' }}>
          Next note: <strong>{noteIndex < NOTES.length ? NOTES[noteIndex].toUpperCase() : '✓'}</strong>
        </p>
      </div>
    </div>
  );
}
