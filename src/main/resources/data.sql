INSERT INTO questions (match_id, question_text, optiona, optionb, optionc, optiond, correct_answer)
SELECT 1, 'Who wins the toss?', teama, teamb, 'No Toss', 'Tie', NULL
FROM matches WHERE id = 1
AND NOT EXISTS (SELECT 1 FROM questions WHERE match_id = 1 AND question_text = 'Who wins the toss?');

INSERT INTO questions (match_id, question_text, optiona, optionb, optionc, optiond, correct_answer)
SELECT 1, 'Who wins the match?', teama, teamb, 'Draw', 'No Result', NULL
FROM matches WHERE id = 1
AND NOT EXISTS (SELECT 1 FROM questions WHERE match_id = 1 AND question_text = 'Who wins the match?');

INSERT INTO questions (match_id, question_text, optiona, optionb, optionc, optiond, correct_answer)
SELECT 1, 'Total runs scored?', '150-175', '176-200', '201-225', '225+', NULL
FROM matches WHERE id = 1
AND NOT EXISTS (SELECT 1 FROM questions WHERE match_id = 1 AND question_text = 'Total runs scored?');

INSERT INTO questions (match_id, question_text, optiona, optionb, optionc, optiond, correct_answer)
SELECT 1, 'Who scores highest runs?', CONCAT(teama, ' Batsman'), CONCAT(teamb, ' Batsman'), 'Draw', 'No Result', NULL
FROM matches WHERE id = 1
AND NOT EXISTS (SELECT 1 FROM questions WHERE match_id = 1 AND question_text = 'Who scores highest runs?');
