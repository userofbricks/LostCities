gamerule commandBlockOutput false

execute if block ~ ~ ~-1 create:track run place template create_lost_cities:track_y_going_e_ne_from_w ~ ~ ~-4
execute if block ~-1 ~ ~ create:track run place template create_lost_cities:track_y_going_n_nw_from_s ~-4 ~ ~
execute if block ~ ~ ~1 create:track run place template create_lost_cities:track_y_going_w_sw_from_e ~ ~ ~
execute if block ~1 ~ ~ create:track run place template create_lost_cities:track_y_going_s_se_from_n ~ ~ ~

clone ~ ~1 ~ ~ ~1 ~ ~ ~ ~