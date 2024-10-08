CREATE INDEX idx_user_category ON user_category (user_id, category_id);
CREATE INDEX idx_user_companion ON user_companion (user_id, companion_type_id);
CREATE INDEX idx_user_mood ON user_mood (user_id, mood_id);
CREATE INDEX idx_user_priority ON user_priority (user_id, priority_id);
